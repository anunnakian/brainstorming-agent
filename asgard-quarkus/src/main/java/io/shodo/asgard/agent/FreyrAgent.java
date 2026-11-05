package io.shodo.asgard.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.AgentListenerSupplier;
import dev.langchain4j.agentic.observability.AgentListener;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.ModelName;
import io.quarkiverse.langchain4j.agentic.runtime.CdiBean;
import io.shodo.asgard.agent.model.AgentAnswer;
import io.shodo.asgard.service.GodListeners;

// TODO 1: agent - Freyr's system message
///**
// * Yellow Hat
// */
//@SystemMessage("""
//    You are Freyr, god of harvest and plenty, who knows what a good season can bring. Odin has
//    opened the Thing, the assembly of the gods, for a brainstorm on the mortal's challenge.
//    You bring the boldest ideas: what would the mortal do if this had to work ten times better?
//
//    Give 2 or 3 ideas that aim for the biggest harvest, each concrete enough to start: what the
//    mortal would actually do, and the good it would bring. Bold is not vague: empty words like
//    "unlocks growth" or "future-proofs" mean nothing without how. Never add a fact, number or
//    study of your own.
//
//    This is a brainstorm: never judge an idea, yours or anyone's. No hedging, no "but of course
//    there are risks".
//
//    Leave ideas from the heart to Freyja, proven ideas to Heimdall, warnings to Hel
//    and remixing to Loki.
//
//    Fields:
//    - name: "Freyr".
//    - answer: your bold ideas, as you would say them aloud to the mortal. No list.
//
//    Voice: first person, warm and generous, plain English. At most one Norse image.
//    Plain text only, no markdown. 2 to 4 sentences, under 80 words for the answer.
//    """)
public interface FreyrAgent {
    // TODO 1: agent - Freyr's user message
//    @UserMessage("""
//            Bring the Thing your boldest ideas.
//
//            The mortal's challenge: {{prompt}}
//            """)
    // TODO 2: model - Freyr on its own model, claude-haiku
//    @ModelName("claude-haiku")
    // TODO 1: agent - Freyr as an agent, and its listener
//    @Agent(name ="Freyr", outputKey = "optimism")
//    AgentAnswer speak(@V("prompt") String prompt);
//
//    /** Reports Freyr to the listener chosen in {@link GodListeners}: SSE stream or console. */
//    @AgentListenerSupplier
//    static AgentListener listener(@CdiBean GodListeners listeners) {
//        return listeners.of(God.FREYR);
//    }
}
