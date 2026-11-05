package io.shodo.asgard.agent;

import dev.langchain4j.model.openai.OpenAiChatModel;
import io.quarkiverse.langchain4j.ModelBuilderCustomizer;
import io.quarkiverse.langchain4j.ModelName;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Grok goes through the OpenAI provider, which always sends presence and frequency penalties
 * (they default to 0 and cannot be unset in application.properties). Grok rejects them, so they
 * are removed from the "grok" model before it is built.
 */
@ApplicationScoped
@ModelName("grok")
public class GrokChatModelCustomizer implements ModelBuilderCustomizer<OpenAiChatModel.OpenAiChatModelBuilder> {
    @Override
    public void customize(OpenAiChatModel.OpenAiChatModelBuilder builder) {
        builder.presencePenalty(null)
               .frequencyPenalty(null);
    }
}
