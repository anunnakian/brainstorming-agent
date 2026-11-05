package io.shodo.asgard.service;

import dev.langchain4j.agentic.scope.AgenticScope;
import io.shodo.asgard.agent.God;
import io.shodo.asgard.api.ThingEvent;
import io.smallrye.mutiny.subscription.MultiEmitter;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Open SSE streams, one per running debate, and who has taken the floor in each. Agent listeners find
 * theirs through the {@code debateId} stored in the agentic scope.
 */
@ApplicationScoped
public class ThingStreams {

    public static final String DEBATE_ID = "debateId";

    private final Map<String, MultiEmitter<? super ThingEvent>> emitters = new ConcurrentHashMap<>();
    /** The gods that have spoken, in the order the planner called them. Odin's opening comes before them. */
    private final Map<String, List<God>> floors = new ConcurrentHashMap<>();

    public void open(String debateId, MultiEmitter<? super ThingEvent> emitter) {
        emitters.put(debateId, emitter);
        floors.put(debateId, new ArrayList<>());
    }

    public void close(String debateId) {
        emitters.remove(debateId);
        floors.remove(debateId);
    }

    /** Does nothing when the agent runs outside a debate, e.g. called directly in a test. */
    public void emit(AgenticScope scope, ThingEvent event) {
        Object debateId = scope.readState(DEBATE_ID);
        MultiEmitter<? super ThingEvent> emitter = debateId == null ? null : emitters.get(debateId.toString());
        if (emitter != null) {
            emitter.emit(event);
        }
    }

    /** Gives the floor to a god: its turn number in the debate, after Odin's opening (turn 1); 0 outside a debate. */
    public int takeFloor(AgenticScope scope, God god) {
        List<God> floor = floor(scope);
        if (floor == null) {
            return 0;
        }
        synchronized (floor) {
            floor.add(god);
            return floor.size() + 1;
        }
    }

    /** The gods that have taken the floor so far, in order; empty outside a debate. */
    public List<God> spoken(AgenticScope scope) {
        List<God> floor = floor(scope);
        if (floor == null) {
            return List.of();
        }
        synchronized (floor) {
            return List.copyOf(floor);
        }
    }

    private List<God> floor(AgenticScope scope) {
        Object debateId = scope.readState(DEBATE_ID);
        return debateId == null ? null : floors.get(debateId.toString());
    }
}
