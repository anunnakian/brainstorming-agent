package io.shodo.asgard.agent.guardrail;

import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailRequest;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Redacts the mortal's personal data before the message reaches the model: the gods never see an email,
 * a phone number, an IBAN or a card number, only a placeholder in its place.
 */
@ApplicationScoped
public class PersonalDataGuardrail implements InputGuardrail {
    /** Most specific first: an IBAN or a card number would otherwise be read as a phone number. */
    private static final Map<Pattern, String> PERSONAL_DATA = new LinkedHashMap<>();

    static {
        PERSONAL_DATA.put(Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"), "[EMAIL]");
        PERSONAL_DATA.put(Pattern.compile("\\b[A-Z]{2}\\d{2}(?: ?[A-Z0-9]{4}){2,7}(?: ?[A-Z0-9]{1,3})?\\b"), "[IBAN]");
        PERSONAL_DATA.put(Pattern.compile("\\b(?:\\d[ -]?){12,18}\\d\\b"), "[CARD]");
        PERSONAL_DATA.put(Pattern.compile("(?<![\\w+])\\+?\\d[\\d .()-]{7,}\\d\\b"), "[PHONE]");
    }

    @Override
    public InputGuardrailResult validate(InputGuardrailRequest request) {
        String text = request.userMessage().singleText();
        String redacted = redact(text);
        return redacted.equals(text) ? success() : successWith(redacted);
    }

    static String redact(String text) {
        String redacted = text;
        for (Map.Entry<Pattern, String> data : PERSONAL_DATA.entrySet()) {
            redacted = data.getKey().matcher(redacted).replaceAll(data.getValue());
        }
        return redacted;
    }
}
