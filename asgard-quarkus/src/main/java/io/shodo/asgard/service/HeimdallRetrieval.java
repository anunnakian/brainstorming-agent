package io.shodo.asgard.service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.quarkus.arc.Unremovable;
import io.smallrye.common.annotation.Identifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.OptionalDouble;

/**
 * Heimdall's retrieval augmentor, built like the one Easy RAG creates (Easy RAG backs off when the application
 * provides its own).
 * <p>
 * It is qualified, so it is not the default bean: AI services do not pick it up on their own, only Heimdall asks for it.
 */
@ApplicationScoped
public class HeimdallRetrieval {
    public static final String HEIMDALL = "heimdall";

    /** Easy RAG's default is 5. */
    @ConfigProperty(name = "quarkus.langchain4j.easy-rag.max-results", defaultValue = "5")
    int maxResults;

    @ConfigProperty(name = "quarkus.langchain4j.easy-rag.min-score")
    OptionalDouble minScore;

    @Produces
    @Unremovable
    @ApplicationScoped
    @Identifier(HEIMDALL)
    RetrievalAugmentor retrievalAugmentor(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        EmbeddingStoreContentRetriever.EmbeddingStoreContentRetrieverBuilder builder = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(maxResults);
        minScore.ifPresent(builder::minScore);
        return DefaultRetrievalAugmentor.builder().contentRetriever(builder.build()).build();
    }
}
