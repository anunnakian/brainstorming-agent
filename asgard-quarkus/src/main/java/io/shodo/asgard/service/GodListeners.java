package io.shodo.asgard.service;

import dev.langchain4j.agentic.observability.AgentListener;
import io.shodo.asgard.agent.God;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * The listener attached to each god's agent, chosen in {@code application.properties}:
 * <pre>
 * asgard.listener=sse      # the debate's SSE stream, which feeds the tablets
 * asgard.listener=console  # the console, in the colour of each hat
 * </pre>
 * Can also be set at launch, e.g. {@code -Dasgard.listener=console}.
 */
@ApplicationScoped
public class GodListeners {

    public enum Kind { SSE, CONSOLE }

    private final ThingStreams streams;
    private final Kind kind;

    public GodListeners(ThingStreams streams,
                        @ConfigProperty(name = "asgard.listener", defaultValue = "sse") String kind) {
        this.streams = streams;
        this.kind = Kind.valueOf(kind.strip().toUpperCase());
    }

    /** The chosen listener for this god. */
    public AgentListener of(God god) {
        return switch (kind) {
            case SSE -> new SseGodListener(god, streams);
            case CONSOLE -> new ConsoleGodListener(god);
        };
    }
}
