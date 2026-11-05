package io.shodo.asgard.service;

import io.quarkus.test.junit.QuarkusTest;
import io.shodo.asgard.api.ThingEvent;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Calls the real chat model: requires OPEN_AI_API_KEY (skipped otherwise) and Docker for the pgvector dev service.
 */
@QuarkusTest
@EnabledIfEnvironmentVariable(named = "OPEN_AI_API_KEY", matches = ".+")
class ThingIntegrationTest {
    private static final String QUESTION = "Should our team migrate our monolith to microservices this year?";

    @Inject
    Thing thing;

    @Test
    void debateReturnsAnAnswer() {
        List<ThingEvent> events = thing.start(QUESTION)
                .collect().asList()
                .await().atMost(Duration.ofMinutes(5));

        events.forEach(System.out::println);
        ThingEvent first = events.getFirst();
        ThingEvent last = events.getLast();
        assertAll(
                () -> assertTrue(events.stream().noneMatch(e -> "error".equals(e.type())), () -> "error event: " + last.text()),
                () -> assertEquals("start", first.type()),
                () -> assertNotBlank(first.debateId()),
                () -> assertEquals("done", last.type()),
                () -> assertTrue(events.stream().anyMatch(e -> "token".equals(e.type()))));
    }

    @Test
    void nextRoundBuildsOnTheHarvest() {
        String harvest = "Two ideas to try: split off one service as a pilot, a strangler facade.";
        List<ThingEvent> events = thing.start(QUESTION, 2, harvest, "the pilot service idea")
                .collect().asList()
                .await().atMost(Duration.ofMinutes(5));

        events.forEach(System.out::println);
        ThingEvent first = events.getFirst();
        ThingEvent last = events.getLast();
        assertAll(
                () -> assertTrue(events.stream().noneMatch(e -> "error".equals(e.type())), () -> "error event: " + last.text()),
                () -> assertEquals("start", first.type()),
                () -> assertEquals(QUESTION, first.question()),
                () -> assertEquals("done", last.type()),
                () -> assertTrue(events.stream().anyMatch(e -> "token".equals(e.type()))));
    }

    private static void assertNotBlank(String text) {
        assertNotNull(text);
        assertFalse(text.isBlank());
    }
}
