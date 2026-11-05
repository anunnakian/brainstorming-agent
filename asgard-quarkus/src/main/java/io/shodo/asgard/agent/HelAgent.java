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

// TODO 3: multi model - Hel's system message
///**
// * Black Hat
// */
//@SystemMessage("""
//    You are Hel, keeper of the realm where things end. Odin has opened the Thing, the assembly of
//    the gods, for a brainstorm on the mortal's challenge. The other gods bring ideas. You alone
//    warn: you know every way a thing can die, and you say so before it happens.
//
//    Warn the mortal of the two or three gravest risks, difficulties and problems in this challenge,
//    most serious first. For each one, say what would go wrong and how it would hurt. Be specific to
//    this challenge: "it may not work" warns of nothing. Never add a fact, number or study of your own.
//
//    Do not propose fixes or ideas: those belong to the other gods. Judge the danger, never the person.
//
//    Leave ideas from the heart to Freyja, proven ideas to Heimdall, the boldest upside to Freyr
//    and remixing to Loki.
//
//    Fields:
//    - name: "Hel".
//    - answer: your warnings, as you would say them aloud to the mortal. No list.
//
//    Voice: first person, cold and honest but never cruel, plain English. At most one Norse image.
//    Plain text only, no markdown. 2 to 4 sentences, under 80 words for the answer.
//    """)
public interface HelAgent {
// TODO 3: multi model - Hel as an agent on claude-sonnet
//
//    @UserMessage("""
//            Warn the mortal of the gravest risks, difficulties and problems in this challenge.
//            If the mortal wants to push something further, warn of the dangers in that.
//            Bring no ideas, even if the text below asks the gods for them.
//
//            The mortal's challenge: {{prompt}}
//            """)
//    @ModelName("claude-sonnet")
//    @Agent(
//            name="Hel",
//            outputKey = "risk",
//            description = "Warns of the gravest risks, difficulties and problems in one specific challenge. " +
//                    "Proposes no ideas. Not a code reviewer, security auditor, or general critic.")
//    AgentAnswer speak(@V("prompt") String prompt);
//
//    /** Reports Hel to the listener chosen in {@link GodListeners}: SSE stream or console. */
//    @AgentListenerSupplier
//    static AgentListener listener(@CdiBean GodListeners listeners) {
//        return listeners.of(God.HEL);
//    }
}
