package io.shodo.asgard.api;

/**
 * From round 2 on, {@code round} counts the brainstorm's rounds, {@code picks} is Odin's picks from the last round
 * and {@code push} what the mortal wants the gods to take further. Round 1 leaves all three empty.
 */
public record StartRequest(String question, Integer round, String picks, String push) {

    /** The round of the brainstorm, 1 when none was sent. */
    public int roundOrFirst() {
        return round == null || round < 1 ? 1 : round;
    }
}
