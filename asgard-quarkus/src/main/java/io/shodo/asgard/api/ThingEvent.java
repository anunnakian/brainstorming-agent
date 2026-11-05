package io.shodo.asgard.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.shodo.asgard.agent.God;

import java.time.Instant;
import java.util.List;

/**
 * One server-sent event of a debate. {@code type} is also used as the SSE event name.
 * <ul>
 *     <li>{@code start} without a god opens the debate, with a god it opens that god's turn</li>
 *     <li>{@code token} is a chunk of the current god's text</li>
 *     <li>{@code end} closes the god's turn, with where its facts come from</li>
 *     <li>{@code done} closes the debate, {@code error} reports a failure</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ThingEvent(String type,
                         God god,
                         String text,
                         String debateId,
                         String question,
                         String hat,
                         Integer order,
                         List<God> heard,
                         String model,
                         Instant startedAt,
                         Long durationMs,
                         Integer calls,
                         Integer tokensIn,
                         Integer tokensOut,
                         List<String> sources) {

    public static ThingEvent debateStart(String debateId, String question) {
        return new ThingEvent("start", null, null, debateId, question, null, null, null, null, null, null, null, null, null, null);
    }

    public static ThingEvent turnStart(God god, int order, List<God> heard, String model, Instant startedAt) {
        return new ThingEvent("start", god, null, null, null, god.hat(), order, heard, model, startedAt, null, null, null, null, null);
    }

    public static ThingEvent token(God god, String text) {
        return new ThingEvent("token", god, text, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public static ThingEvent turnEnd(God god, long durationMs, int tokensIn, int tokensOut) {
        return turnEnd(god, durationMs, tokensIn, tokensOut, null);
    }

    /** {@code sources}: where the god's facts come from, left out when empty. */
    public static ThingEvent turnEnd(God god, long durationMs, int tokensIn, int tokensOut, List<String> sources) {
        return new ThingEvent("end", god, null, null, null, null, null, null, null, null, durationMs, null, tokensIn, tokensOut,
                filled(sources));
    }

    public static ThingEvent done(String debateId, long durationMs, int calls, int tokensIn, int tokensOut) {
        return new ThingEvent("done", null, null, debateId, null, null, null, null, null, null, durationMs, calls, tokensIn, tokensOut, null);
    }

    public static ThingEvent error(God god, String text) {
        return new ThingEvent("error", god, text, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    /** The non-blank lines, or {@code null} when there are none, so the field is left out. */
    private static List<String> filled(List<String> lines) {
        if (lines == null) {
            return null;
        }
        List<String> filled = lines.stream().filter(line -> line != null && !line.isBlank()).map(String::strip).toList();
        return filled.isEmpty() ? null : filled;
    }
}
