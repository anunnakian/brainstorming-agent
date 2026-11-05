package io.shodo.asgard.agent;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import io.quarkiverse.langchain4j.ModelName;
import io.quarkiverse.langchain4j.evaluation.junit5.Evaluate;
import io.quarkiverse.langchain4j.evaluation.junit5.SampleLocation;
import io.quarkiverse.langchain4j.evaluation.junit5.ScorerConfiguration;
import io.quarkiverse.langchain4j.testing.evaluation.EvaluationReport;
import io.quarkiverse.langchain4j.testing.evaluation.Parameters;
import io.quarkiverse.langchain4j.testing.evaluation.Samples;
import io.quarkiverse.langchain4j.testing.evaluation.Scorer;
import io.quarkiverse.langchain4j.testing.evaluation.judge.AiJudgeStrategy;
import io.quarkiverse.langchain4j.testing.evaluation.similarity.SemanticSimilarityStrategy;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.quarkiverse.langchain4j.testing.evaluation.EvaluationAssertions.assertThat;

@QuarkusTest
@Evaluate
class FreyrAgentEvaluationIntegrationTest {
    /** Starting point, to calibrate: Scorer logs each sample's similarity as its score. */
    private static final double MIN_SIMILARITY = 0.55;

    /** At least 2 of the 3 samples must pass: a model's output varies from one run to the next. */
    private static final double MIN_SCORE = 60.0;

    /** AiJudgeStrategy parses the reply with Boolean.parseBoolean: anything but a bare "true" is a failure. */
    private static final String JUDGE_PROMPT = """
            You are evaluating the response of a brainstorming agent against the expected output, which
            describes what a good response does. Be strict, but judge only what the expected output asks for.

            Response to evaluate: {response}
            Expected output: {expected_output}

            Reply with exactly one word and no punctuation: true if the response satisfies all of the
            expected output, false otherwise.
            """;

    @Inject
    FreyrAgent optimisticAgent;

    @Inject
    EmbeddingModel embeddingModel;

    /** Freyr runs on Claude, so the judge runs on GPT: a judge never grades its own model family. */
    @Inject
    @ModelName("gpt-sol")
    ChatModel judgeModel;

    @Test
    void should_stay_close_to_reference_answers(
            @ScorerConfiguration(concurrency = 3) Scorer scorer,
            @SampleLocation("src/test/resources/evaluation/freyr-similarity-samples.yaml") Samples<String> samples) {
        // when
        EvaluationReport<String> report = scorer.evaluate(samples, this::speak,
                new SemanticSimilarityStrategy(embeddingModel, MIN_SIMILARITY));

        // then
        assertThat(report).hasScoreGreaterThanOrEqualTo(MIN_SCORE);
    }

    @Test
    void should_satisfy_the_ai_judge(
            @ScorerConfiguration(concurrency = 3) Scorer scorer,
            @SampleLocation("src/test/resources/evaluation/freyr-judge-samples.yaml") Samples<String> samples) {
        // when
        EvaluationReport<String> report = scorer.evaluate(samples, this::speak,
                new AiJudgeStrategy(judgeModel, JUDGE_PROMPT));

        // then
        assertThat(report).hasScoreGreaterThanOrEqualTo(MIN_SCORE);
    }

    private String speak(Parameters parameters) {
        return optimisticAgent.speak(parameters.get(0)).getAnswer();
    }
}
