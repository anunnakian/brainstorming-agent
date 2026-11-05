package io.shodo.asgard.service;

import dev.langchain4j.agentic.observability.AgentInvocationError;
import dev.langchain4j.agentic.observability.AgentListener;
import dev.langchain4j.agentic.observability.AgentRequest;
import dev.langchain4j.agentic.observability.AgentResponse;
import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.model.output.TokenUsage;
import io.shodo.asgard.agent.God;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.shodo.asgard.service.GodWords.*;

/**
 * Writes the invocations of one god's agent to the console, in the colour of the god's hat:
 * when it takes the floor, what it says, and how long and how many tokens it took.
 */
public class ConsoleGodListener implements AgentListener {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";

    private final God god;
    private final Map<AgenticScope, Instant> startedAt = new ConcurrentHashMap<>();

    public ConsoleGodListener(God god) {
        this.god = god;
    }

    @Override
    public void beforeAgentInvocation(AgentRequest request) {
        startedAt.put(request.agenticScope(), Instant.now());
        print("takes the floor");
    }

    @Override
    public void afterAgentInvocation(AgentResponse response) {
        Instant start = startedAt.remove(response.agenticScope());
        long durationMs = start == null ? 0 : Duration.between(start, Instant.now()).toMillis();
        TokenUsage usage = response.chatResponse() == null ? null : response.chatResponse().tokenUsage();
        Object output = response.output();

        print("says: " + text(output));
        lines("source", sources(output));
        print("done in %d ms, %s tokens in, %s tokens out".formatted(durationMs,
                usage == null ? "?" : usage.inputTokenCount(),
                usage == null ? "?" : usage.outputTokenCount()));
    }

    @Override
    public void onAgentInvocationError(AgentInvocationError error) {
        startedAt.remove(error.agenticScope());
        print("fails: " + error.error().getMessage());
    }

    private void lines(String label, List<String> lines) {
        if (lines != null) {
            lines.stream().filter(line -> line != null && !line.isBlank())
                    .forEach(line -> print("  " + label + ": " + line.strip()));
        }
    }

    private void print(String message) {
        System.out.println(colour() + BOLD + "[" + god.name() + "] " + RESET + colour() + message + RESET);
    }

    /** ANSI colour of the god's hat. */
    private String colour() {
        return switch (god.hat()) {
            case "blue" -> "\u001B[34m";
            case "green" -> "\u001B[32m";
            case "yellow" -> "\u001B[33m";
            case "black" -> "\u001B[90m";
            case "red" -> "\u001B[31m";
            default -> "\u001B[37m";
        };
    }
}
