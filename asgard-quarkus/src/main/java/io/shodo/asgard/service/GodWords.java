package io.shodo.asgard.service;

import dev.langchain4j.service.Result;
import io.shodo.asgard.agent.model.AgentAnswer;
import io.shodo.asgard.agent.model.FactAgentAnswer;

import java.util.List;

/**
 * What a god's agent returned, read the same way by every listener.
 */
public final class GodWords {

    private GodWords() {
    }

    /** What a god said: the answer of a structured output, or the text itself. */
    public static String text(Object output) {
        return switch (output) {
            case null -> "";
            case AgentAnswer answer -> answer.getAnswer();
            case FactAgentAnswer facts -> facts.getAnswer();
            case Result<?> result -> String.valueOf(result.content());
            default -> String.valueOf(output);
        };
    }

    /** Where Heimdall's facts come from: URLs or document names. */
    public static List<String> sources(Object output) {
        return output instanceof FactAgentAnswer facts ? facts.getSources() : null;
    }
}
