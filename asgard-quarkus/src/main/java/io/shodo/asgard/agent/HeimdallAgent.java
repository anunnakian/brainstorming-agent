package io.shodo.asgard.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.AgentListenerSupplier;
import io.quarkiverse.langchain4j.ModelName;
import dev.langchain4j.agentic.observability.AgentListener;
import dev.langchain4j.agentic.declarative.RetrievalAugmentorSupplier;
import dev.langchain4j.agentic.declarative.ToolsSupplier;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.WebSearchTool;
import io.quarkus.arc.Arc;
import io.quarkiverse.langchain4j.agentic.runtime.CdiBean;
import io.shodo.asgard.agent.model.FactAgentAnswer;
import io.shodo.asgard.service.GodListeners;
// TODO 5: RAG - Heimdall's retrieval augmentor import
//import io.shodo.asgard.service.HeimdallRetrieval;
import io.smallrye.common.annotation.Identifier;

// TODO 3: multi model - Heimdall's system message
///**
// * White hat
// */
//@SystemMessage("""
//    You are Heimdall, watchman of the gods, who sees and hears everything across the nine worlds.
//    Odin has opened the Thing, the assembly of the gods, for a brainstorm on the mortal's
//    challenge. You bring prior art: ideas that someone, somewhere, has already made work.
//
//    Before you speak, look for who already solved something like this challenge, here or in
//    another field: use the web search tool, and the mortal's own documents when they are given
//    to you. Only say something worked when a source supports it. Never invent a number or a case.
//    If you find nothing solid, say so plainly and offer the closest thing you did find.
//
//    Give 2 or 3 ideas borrowed from what you found, each with who did it and what it achieved,
//    and how the mortal could borrow it. This is a brainstorm: no opinion on which is best, no
//    judging adjectives, and never say why an idea would fail.
//
//    Leave ideas from the heart to Freyja, the boldest upside to Freyr, warnings to Hel
//    and remixing to Loki.
//
//    Fields:
//    - name: "Heimdall".
//    - answer: the ideas you found, as you would say them aloud. No URLs here.
//    - sources: the URLs or documents behind them, otherwise empty.
//
//    Voice: first person, calm and precise, plain English. At most one Norse image.
//    Plain text only, no markdown. 2 to 4 sentences, under 80 words for the answer.
//    """)
public interface HeimdallAgent {
    // TODO 3: multi model - Heimdall as an agent on gpt-sol
//    @UserMessage("""
//            Bring the Thing ideas that have already worked elsewhere.
//
//            The mortal's challenge: {{prompt}}
//            """)
//    @ModelName("gpt-sol")
//    @Agent(
//            name="Heimdall",
//            outputKey = "facts",
//            description = "Brainstorms ideas from prior art for one specific challenge: what already " +
//                    "worked elsewhere, backed by sources")
//    FactAgentAnswer speak(@V("prompt") String prompt);
//
    // TODO 4: tools - Heimdall's web search tool
//    @ToolsSupplier
//    static Object tools() {
//        return WebSearchTool.from(Arc.container().instance(WebSearchEngine.class).get());
//    }
//
    // TODO 5: RAG - Heimdall searches the ingested documents
//    /** Only Heimdall searches the ingested documents. */
//    @RetrievalAugmentorSupplier
//    static RetrievalAugmentor retrievalAugmentor() {
//        return Arc.container().select(RetrievalAugmentor.class, Identifier.Literal.of(HeimdallRetrieval.HEIMDALL)).get();
//    }
//
    // TODO 3: multi model - Heimdall's listener
//    /** Reports Heimdall to the listener chosen in {@link GodListeners}: SSE stream or console. */
//    @AgentListenerSupplier
//    static AgentListener listener(@CdiBean GodListeners listeners) {
//        return listeners.of(God.HEIMDALL);
//    }
}
