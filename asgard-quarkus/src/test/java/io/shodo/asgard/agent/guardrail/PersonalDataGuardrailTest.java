package io.shodo.asgard.agent.guardrail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonalDataGuardrailTest {
    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "Write to odin.allfather@asgard.io for details. | Write to [EMAIL] for details.",
            "Call me on +46 70 123 45 67 tonight.           | Call me on [PHONE] tonight.",
            "Or on 06.12.34.56.78 in France.                | Or on [PHONE] in France.",
            "Pay to SE45 5000 0000 0583 9825 7466 please.   | Pay to [IBAN] please.",
            "My card is 4111 1111 1111 1111, keep it safe.  | My card is [CARD], keep it safe."})
    void redactsPersonalData(String text, String redacted) {
        assertEquals(redacted, PersonalDataGuardrail.redact(text));
    }

    @Test
    void redactsEveryPieceOfPersonalDataInOneMessage() {
        String text = "I'm Ragnar, ragnar@kattegat.no, +47 912 34 567: how do we get more people to our meetup?";

        assertEquals("I'm Ragnar, [EMAIL], [PHONE]: how do we get more people to our meetup?",
                PersonalDataGuardrail.redact(text));
    }

    @Test
    void leavesAnHonestQuestionUntouched() {
        String question = "How might we get 200 people to our monthly meetup in 2026, starting on the 3rd of May?";

        assertEquals(question, PersonalDataGuardrail.redact(question));
    }
}
