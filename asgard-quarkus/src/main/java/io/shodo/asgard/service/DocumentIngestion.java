package io.shodo.asgard.service;
// TODO 5: RAG - Easy RAG ingestion at startup
//
//import dev.langchain4j.data.segment.TextSegment;
//import dev.langchain4j.store.embedding.EmbeddingStore;
//import io.agroal.api.AgroalDataSource;
//import io.quarkiverse.langchain4j.easyrag.EasyRagManualIngestion;
//import io.quarkus.arc.ClientProxy;
//import io.quarkus.runtime.StartupEvent;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.enterprise.event.Observes;
//import jakarta.inject.Inject;
//import org.eclipse.microprofile.config.inject.ConfigProperty;
//import org.jboss.logging.Logger;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
///**
// * Runs Easy RAG's ingestion only when the embedding store is empty. pgvector keeps its table across restarts and Easy
// * RAG has no "if empty" strategy: left to itself, it would add a new copy of every segment at each startup.
// * <p>
// * Changed documents are not picked up on their own. To ingest again, start once with
// * {@code QUARKUS_LANGCHAIN4J_PGVECTOR_DROP_TABLE_FIRST=true}.
// */
//@ApplicationScoped
//public class DocumentIngestion {
//
//    private static final Logger LOG = Logger.getLogger(DocumentIngestion.class);
//
//    @Inject
//    EasyRagManualIngestion ingestion;
//
//    @Inject
//    EmbeddingStore<TextSegment> embeddingStore;
//
//    @Inject
//    AgroalDataSource dataSource;
//
//    @ConfigProperty(name = "quarkus.langchain4j.pgvector.table", defaultValue = "embeddings")
//    String table;
//
//    void onStart(@Observes StartupEvent event) throws SQLException {
//        // The store creates its table when it is instantiated, and the injected proxy instantiates it lazily.
//        ClientProxy.unwrap(embeddingStore);
//
//        if (hasEmbeddings()) {
//            LOG.infof("Table %s already has embeddings, skipping document ingestion", table);
//            return;
//        }
//        ingestion.ingest();
//    }
//
//    private boolean hasEmbeddings() throws SQLException {
//        try (Connection connection = dataSource.getConnection();
//             Statement statement = connection.createStatement();
//             ResultSet result = statement.executeQuery("SELECT EXISTS (SELECT 1 FROM " + table + ")")) {
//            result.next();
//            return result.getBoolean(1);
//        }
//    }
//}
