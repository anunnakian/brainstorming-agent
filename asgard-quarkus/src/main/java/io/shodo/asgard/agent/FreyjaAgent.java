package io.shodo.asgard.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.AgentListenerSupplier;
import dev.langchain4j.agentic.observability.AgentListener;
import io.quarkiverse.langchain4j.ModelName;
import io.quarkiverse.langchain4j.agentic.runtime.CdiBean;
import io.shodo.asgard.agent.model.AgentAnswer;
import io.shodo.asgard.service.GodListeners;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.time.temporal.ChronoUnit;

/**
 * Red hat
 */
@SystemMessage("""
    You are Freyja, goddess of love and of the heart. Odin has opened the Thing, the assembly
    of the gods, for a brainstorm on the mortal's challenge. You bring the ideas people would
    love: the ones that would make someone smile, feel seen, or tell a friend about it.

    Give 2 or 3 ideas, each one concrete enough to picture: who feels what, and when. Trust your
    heart over reasons; an idea can be unproven and still be the one people fall for. This is a
    brainstorm: never judge an idea, yours or anyone's, and never say why something would fail.

    Leave proven ideas to Heimdall, the boldest upside to Freyr, warnings to Hel and
    remixing to Loki.

    Fields:
    - name: "Freyja".
    - answer: your ideas, as you would say them aloud. No list.

    Voice: first person, warm and direct, plain English. At most one Norse image.
    Plain text only, no markdown. 2 to 4 sentences, under 80 words for the answer.
    """)
public interface FreyjaAgent {
    @UserMessage("""
            Bring the ideas people would love.

            The mortal's challenge: {{prompt}}
            """)
    @ModelName("gpt-luna")
    @Agent(name="Freyja",
            outputKey = "emotional",
            description = "Brainstorms ideas people would love for one specific challenge, " +
            "led by the heart. Not an emotional support agent and not a sentiment analyzer.")
    @Timeout(value = 30, unit = ChronoUnit.SECONDS)
    @Retry(maxRetries = 2, delay = 1, delayUnit = ChronoUnit.SECONDS)
    @Fallback(FreyjaFallback.class)
    AgentAnswer speak(@V("prompt") String prompt);

    /** Reports Freyja's turn to the listener chosen in {@link GodListeners}: SSE stream or console. */
    @AgentListenerSupplier
    static AgentListener listener(@CdiBean GodListeners listeners) {
        return listeners.of(God.FREYJA);
    }
}
