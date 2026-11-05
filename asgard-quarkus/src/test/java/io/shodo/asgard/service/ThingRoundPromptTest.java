package io.shodo.asgard.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThingRoundPromptTest {
    private static final String QUESTION = "How might we get more people to come to our monthly developer meetup?";
    private static final String HARVEST = "Two ideas to try: a shared poll with the neighbouring meetup, a buddy at the door.";
    private static final String PUSH = "the shared poll idea";

    @Test
    void should_carry_the_question_the_harvest_and_the_push() {
        // when
        String prompt = Thing.roundPrompt("  " + QUESTION + "\n", 2, HARVEST + "\n", " " + PUSH);

        // then
        assertAll(
                () -> assertTrue(prompt.startsWith(QUESTION), prompt),
                () -> assertTrue(prompt.contains("round 2 of the brainstorm"), prompt),
                () -> assertTrue(prompt.contains(HARVEST), prompt),
                () -> assertTrue(prompt.contains("push this further: " + PUSH), prompt),
                () -> assertTrue(prompt.contains("Do not repeat ideas already picked"), prompt),
                () -> assertTrue(prompt.contains("Hel alone brings no ideas"), prompt));
    }
}
