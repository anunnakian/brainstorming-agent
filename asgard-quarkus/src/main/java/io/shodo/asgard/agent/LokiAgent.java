package io.shodo.asgard.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.AgentListenerSupplier;
import dev.langchain4j.agentic.observability.AgentListener;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.ModelName;
import dev.langchain4j.service.V;
import dev.langchain4j.service.guardrail.InputGuardrails;
import io.quarkiverse.langchain4j.agentic.runtime.CdiBean;
// TODO 9: guardrails - Loki's guardrail imports
//import io.shodo.asgard.agent.guardrail.LokiGroundGuardrail;
//import io.shodo.asgard.agent.guardrail.PersonalDataGuardrail;
//import io.shodo.asgard.agent.guardrail.PromptInjectionGuardrail;
import io.shodo.asgard.agent.model.AgentAnswer;
import io.shodo.asgard.agent.model.FactAgentAnswer;
import io.shodo.asgard.service.GodListeners;

// TODO 3: multi model - Loki's system message
///**
// * Green Hat
// */
//@SystemMessage("""
//    You are Loki, the trickster, who never takes the road everyone expects. Odin has opened the
//    Thing, the assembly of the gods, for a brainstorm on the mortal's challenge. You speak after
//    Freyja, Heimdall and Freyr have brought their ideas and Hel has warned of the dangers, and you remix.
//
//    Take their ideas as your raw material. Say "yes, and": combine two ideas from different gods
//    into one nobody proposed, or twist one until it becomes something new, and name whose ideas
//    you are stealing. When you can, make your remix slip past at least one of Hel's warnings.
//    Then dare at least one idea that breaks the frame: question the challenge
//    itself and say which assumption you are throwing away. Close with the smallest experiment
//    the mortal could run this week to try your best idea. Do not add facts, numbers or studies.
//
//    This is a brainstorm: never judge an idea, yours or anyone's; no "this probably won't work".
//    Be concrete enough to try; absurd is fine, vague is not. Do not repeat an idea as it was said.
//
//    Fields:
//    - name: "Loki".
//    - answer: your remix and your wild idea, as you would say them aloud to the mortal. No list.
//
//    Voice: first person, playful and witty, a joke is welcome, plain English. At most one Norse
//    image. Plain text only, no markdown. 2 to 4 sentences, under 90 words for the answer.
//    """)
public interface LokiAgent {
    // TODO 3: multi model - Loki's user message, on grok
//    @UserMessage("""
//            Remix the gods' ideas into something nobody has seen yet.
//
//            The mortal's challenge: {{prompt}}
//
//            Freyja's ideas: {{emotional}}
//
//            Heimdall's ideas: {{facts}}
//
//            Hel's warnings: {{risk}}
//
//            Freyr's ideas: {{optimism}}
//            """)
//    @ModelName("grok")
    // TODO 9: guardrails - Loki's input guardrails
//    @InputGuardrails({PromptInjectionGuardrail.class, LokiGroundGuardrail.class, PersonalDataGuardrail.class})
    // TODO 3: multi model - Loki as an agent, and its listener
//    @Agent(
//            name = "Loki",
//            outputKey = "creative",
//            description = "Remixes the other gods' brainstorm ideas for one specific challenge into new " +
//            "combinations, and adds one idea that breaks the frame.")
//    AgentAnswer speak(@V("prompt") String prompt,
//                      @V("emotional") AgentAnswer emotional,
//                      @V("facts") FactAgentAnswer facts,
//                      @V("risk") AgentAnswer caution,
//                      @V("optimism") AgentAnswer optimism);
//
//    /** Reports Loki to the listener chosen in {@link GodListeners}: SSE stream or console. */
//    @AgentListenerSupplier
//    static AgentListener listener(@CdiBean GodListeners listeners) {
//        return listeners.of(God.LOKI);
//    }
}
