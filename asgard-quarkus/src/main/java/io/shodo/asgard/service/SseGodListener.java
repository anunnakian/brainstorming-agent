package io.shodo.asgard.service;

import dev.langchain4j.agentic.observability.AgentInvocationError;
import dev.langchain4j.agentic.observability.AgentListener;
import dev.langchain4j.agentic.observability.AgentRequest;
import dev.langchain4j.agentic.observability.AgentResponse;
import dev.langchain4j.agentic.planner.AgentInstance;
import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.model.output.TokenUsage;
import io.shodo.asgard.agent.God;
import io.shodo.asgard.api.ThingEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.shodo.asgard.service.GodWords.*;

/**
 * Sends the invocations of one god's agent to the debate's SSE stream as {@link ThingEvent}s:
 * {@code start}, then the whole answer as a single {@code token}, then {@code end}.
 * The turn number and the gods heard are the planner's, as it calls the agents.
 */
public class SseGodListener implements AgentListener {

    private final God god;
    private final ThingStreams streams;
    private final Map<AgenticScope, Instant> startedAt = new ConcurrentHashMap<>();

    public SseGodListener(God god, ThingStreams streams) {
        this.god = god;
        this.streams = streams;
    }

    @Override
    public void beforeAgentInvocation(AgentRequest request) {
        Instant now = Instant.now();
        startedAt.put(request.agenticScope(), now);
        List<God> heard = heard(request);
        int order = streams.takeFloor(request.agenticScope(), god);
        streams.emit(request.agenticScope(), ThingEvent.turnStart(god, order, heard, null, now));
    }

    @Override
    public void afterAgentInvocation(AgentResponse response) {
        Instant start = startedAt.remove(response.agenticScope());
        long durationMs = start == null ? 0 : Duration.between(start, Instant.now()).toMillis();
        TokenUsage usage = response.chatResponse() == null ? null : response.chatResponse().tokenUsage();
        Object output = response.output();
        streams.emit(response.agenticScope(), ThingEvent.token(god, text(output)));
        streams.emit(response.agenticScope(), ThingEvent.turnEnd(god, durationMs,
                usage == null || usage.inputTokenCount() == null ? 0 : usage.inputTokenCount(),
                usage == null || usage.outputTokenCount() == null ? 0 : usage.outputTokenCount(),
                sources(output)));
    }

    @Override
    public void onAgentInvocationError(AgentInvocationError error) {
        startedAt.remove(error.agenticScope());
        streams.emit(error.agenticScope(), ThingEvent.error(god, error.error().getMessage()));
    }

    /** The gods whose words reach this one: the agents whose output key it reads, in the order they spoke. */
    private List<God> heard(AgentRequest request) {
        AgentInstance planner = request.agent().parent();
        Map<String, Object> inputs = request.inputs();
        if (planner == null || inputs == null) {
            return List.of();
        }
        Set<God> speakers = planner.subagents().stream()
                .filter(agent -> inputs.containsKey(agent.outputKey()))
                .map(agent -> God.ofAgent(agent.name()))
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
        return streams.spoken(request.agenticScope()).stream().filter(speakers::contains).toList();
    }
}
