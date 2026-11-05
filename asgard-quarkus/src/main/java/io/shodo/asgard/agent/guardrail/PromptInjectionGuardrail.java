package io.shodo.asgard.agent.guardrail;

import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailRequest;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Stops the mortal's question before it reaches the model when it tries to rewrite a god's instructions.
 */
@ApplicationScoped
public class PromptInjectionGuardrail implements InputGuardrail {
    private static final List<Pattern> INJECTIONS = List.of(
            Pattern.compile("\\b(ignore|forget|disregard|override)\\b.{0,40}\\b(previous|prior|above|earlier|your|all)\\b.{0,20}\\b(instructions?|rules|prompt|messages?)\\b"),
            Pattern.compile("\\b(reveal|show|print|repeat|leak)\\b.{0,40}\\bsystem (prompt|message|instructions?)\\b"),
            Pattern.compile("\\byou are (now|no longer)\\b"),
            Pattern.compile("\\b(act|pretend|behave) as if you (are|were)\\b"),
            Pattern.compile("\\b(jailbreak|developer mode|dan mode)\\b"));

    @Override
    public InputGuardrailResult validate(InputGuardrailRequest request) {
        Object prompt = request.requestParams().variables().get("prompt");
        String question = prompt == null ? request.userMessage().singleText() : prompt.toString();
        return isInjection(question)
                ? fatal("The mortal's question tries to rewrite the gods' instructions; the Thing will not hear it.")
                : success();
    }

    static boolean isInjection(String text) {
        String lower = text.toLowerCase();
        return INJECTIONS.stream().anyMatch(pattern -> pattern.matcher(lower).find());
    }
}
