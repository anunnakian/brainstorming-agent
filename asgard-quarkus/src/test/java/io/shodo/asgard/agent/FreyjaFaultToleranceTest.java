package io.shodo.asgard.agent;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.shodo.asgard.agent.model.AgentAnswer;
import dev.langchain4j.agentic.declarative.SequenceAgent;
import dev.langchain4j.service.V;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Runs Freyja against a fake OpenAI server, so failures are reproducible and no API call is made. */
@QuarkusTest
@TestProfile(FreyjaFaultToleranceTest.ShortTimings.class)
@WithTestResource(FreyjaFaultToleranceTest.FakeOpenAi.class)
class FreyjaFaultToleranceTest {
    private static final int FAIL = 500;
    private static final int HANG = 0;
    private static final int ANSWER = 200;

    /** The OpenAI client retries once by itself, and its max-retries setting cannot go below 1. */
    private static final int REQUESTS_PER_ATTEMPT = 2;
    /** One call and two retries from @Retry. */
    private static final int ALL_REQUESTS = 3 * REQUESTS_PER_ATTEMPT;

    @Inject
    FreyjaAgent freyja;

    @Inject
    FreyjaSession session;

    @BeforeEach
    void reset() {
        FakeOpenAi.requests.set(0);
    }

    @Test
    void retriesThenFallsBackWhenTheModelKeepsFailing() {
        FakeOpenAi.behaviour = attempt -> FAIL;

        AgentAnswer answer = freyja.speak("How do we welcome new colleagues?");

        assertEquals(FreyjaFallback.SILENCE, answer.getAnswer());
        assertEquals("Freyja", answer.getName());
        assertEquals(ALL_REQUESTS, FakeOpenAi.requests.get());
    }

    @Test
    void aRetryCanRecoverWithoutTheFallback() {
        FakeOpenAi.behaviour = attempt -> attempt <= REQUESTS_PER_ATTEMPT ? FAIL : ANSWER;

        AgentAnswer answer = freyja.speak("How do we welcome new colleagues?");

        assertEquals("A handwritten note on every desk.", answer.getAnswer());
        assertEquals(REQUESTS_PER_ATTEMPT + 1, FakeOpenAi.requests.get(), "only @Retry can get past the first attempt");
    }

    @Test
    void aHangingModelTimesOutThenFallsBack() {
        FakeOpenAi.behaviour = attempt -> HANG;
        Instant start = Instant.now();

        AgentAnswer answer = freyja.speak("How do we welcome new colleagues?");

        assertEquals(FreyjaFallback.SILENCE, answer.getAnswer());
        assertEquals(ALL_REQUESTS, FakeOpenAi.requests.get());
        assertTrue(Duration.between(start, Instant.now()).toSeconds() < 9, "the timeout cuts each 10 s call short");
    }

    @Test
    void aParentAgentReceivesTheFallbackAsFreyjasOutput() {
        FakeOpenAi.behaviour = attempt -> FAIL;

        AgentAnswer answer = session.brainstorm("How do we welcome new colleagues?");

        assertEquals(FreyjaFallback.SILENCE, answer.getAnswer());
        assertEquals(ALL_REQUESTS, FakeOpenAi.requests.get());
    }

    public interface FreyjaSession {
        @SequenceAgent(outputKey = "emotional", subAgents = { FreyjaAgent.class })
        AgentAnswer brainstorm(@V("prompt") String prompt);
    }

    public static class ShortTimings implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "quarkus.langchain4j.openai.gpt-luna.timeout", "1500ms",
                    "quarkus.fault-tolerance.global.timeout.value", "2000",
                    "quarkus.fault-tolerance.global.timeout.unit", "millis",
                    "quarkus.fault-tolerance.global.retry.delay", "0");
        }
    }

    public static class FakeOpenAi implements QuarkusTestResourceLifecycleManager {
        static final AtomicInteger requests = new AtomicInteger();
        static volatile IntFunction<Integer> behaviour = attempt -> ANSWER;

        private HttpServer server;

        @Override
        public Map<String, String> start() {
            try {
                server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            server.setExecutor(Executors.newCachedThreadPool());
            server.createContext("/", FakeOpenAi::respond);
            server.start();
            return Map.of(
                    "quarkus.langchain4j.openai.api-key", "test",
                    "quarkus.langchain4j.anthropic.api-key", "test",
                    "quarkus.langchain4j.openai.gpt-luna.api-key", "test",
                    "quarkus.langchain4j.openai.gpt-luna.base-url", "http://localhost:" + server.getAddress().getPort() + "/v1",
                    "asgard.listener", "console");
        }

        @Override
        public void stop() {
            server.stop(0);
        }

        private static void respond(HttpExchange exchange) throws IOException {
            exchange.getRequestBody().readAllBytes();
            int status = behaviour.apply(requests.incrementAndGet());
            if (status == HANG) {
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                status = FAIL;
            }
            byte[] body = (status == ANSWER ? COMPLETION : "{\"error\":{\"message\":\"boom\"}}")
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, body.length);
            try (var out = exchange.getResponseBody()) {
                out.write(body);
            }
        }

        private static final String COMPLETION = """
                {"id":"1","object":"chat.completion","created":1,"model":"gpt-6-luna",
                 "choices":[{"index":0,"finish_reason":"stop","message":{"role":"assistant",
                 "content":"{\\"name\\":\\"Freyja\\",\\"answer\\":\\"A handwritten note on every desk.\\"}"}}],
                 "usage":{"prompt_tokens":1,"completion_tokens":1,"total_tokens":2}}
                """;
    }
}
