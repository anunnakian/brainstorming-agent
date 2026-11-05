package io.shodo.asgard.service;

import dev.langchain4j.service.Result;
import io.shodo.asgard.agent.God;
import io.shodo.asgard.api.ThingEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static io.shodo.asgard.agent.God.*;

/**
 * State of one running debate.
 */
public class Debate {


    private static final String ODIN_OPENING = "Welcome, all of you. A traveller has come to us with this: \"%s\". Bring many ideas, judge none of each other's, and build on them. Hel, you alone will name the dangers. I will pick the best ideas at the end.";

    private static final String ODIN_REOPENING = "Round %d. The traveller wants us to push this further: \"%s\". Build on the ideas we have, and bring me ideas that take it further. Hel, name the dangers in it.";

    private static final String MODEL = "gpt-4o-mini";
    private static final Duration TOKEN_DELAY = Duration.ofMillis(40);


    final String id;
    final String question;
    final long startedAt = System.currentTimeMillis();
    final List<God> heard = new ArrayList<>();
    int wordsSpoken = 0;
    int calls = 0;
    int tokensIn = 0;
    int tokensOut = 0;
    String answer;

    Debate(String question) {
        this.id = UUID.randomUUID().toString();
        this.question = question;
    }

    public String getId() {
        return id;
    }
    public Debate(String id, String question) {
        this.id = id;
        this.question = question;
    }

    /** Records the outcome of the six hats session. */
    public void conclude(Result<String> answer) {
        this.answer = answer.content();
        this.tokensIn += answer.tokenUsage().inputTokenCount();
        this.tokensOut += answer.tokenUsage().outputTokenCount();
    }

    public Multi<ThingEvent> turn(God god, int order) {
        return turn(god, order, ODIN_OPENING.formatted(question));
    }

    /** Odin's canned reopening of a next round. */
    public Multi<ThingEvent> reopen(int round, String push) {
        return turn(ODIN, 1, ODIN_REOPENING.formatted(round, push));
    }

    /** Streams a line word by word. */
    public Multi<ThingEvent> turn(God god, int order, String line) {
        List<String> words = Arrays.asList(line.split("(?<= )"));
        Instant turnStartedAt = Instant.now();

        Multi<ThingEvent> start = Multi.createFrom().item(() ->
                ThingEvent.turnStart(god, order, List.copyOf(heard), MODEL, turnStartedAt));
        Multi<ThingEvent> tokens = Multi.createFrom().iterable(words)
                .onItem().call(w -> Uni.createFrom().voidItem().onItem().delayIt().by(TOKEN_DELAY))
                .map(w -> ThingEvent.token(god, w));
        Multi<ThingEvent> end = Multi.createFrom().item(() -> {
            int in = wordCount(question) + wordsSpoken;
            int out = words.size();
            calls++;
            tokensIn += in;
            tokensOut += out;
            wordsSpoken += out;
            if (god != ODIN) {
                heard.add(god);
            }
            return ThingEvent.turnEnd(god, Duration.between(turnStartedAt, Instant.now()).toMillis(), in, out);
        });
        return Multi.createBy().concatenating().streams(start, tokens, end);
    }

    public ThingEvent done() {
        return ThingEvent.done(id, System.currentTimeMillis() - startedAt, calls, tokensIn, tokensOut);
    }

    private static int wordCount(String text) {
        return text.isBlank() ? 0 : text.trim().split("\\s+").length;
    }
}