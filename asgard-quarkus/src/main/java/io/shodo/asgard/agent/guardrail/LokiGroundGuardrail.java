package io.shodo.asgard.agent.guardrail;
// TODO 9: guardrails - Loki needs ideas to remix
//
//import dev.langchain4j.guardrail.InputGuardrail;
//import dev.langchain4j.guardrail.InputGuardrailRequest;
//import dev.langchain4j.guardrail.InputGuardrailResult;
//import io.shodo.asgard.agent.model.AgentAnswer;
//import io.shodo.asgard.agent.model.FactAgentAnswer;
//import jakarta.enterprise.context.ApplicationScoped;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * Loki remixes the other gods' ideas: when none of them has brought one, he would invent from nothing, so he does not speak.
// */
//@ApplicationScoped
//public class LokiGroundGuardrail implements InputGuardrail {
//
//    private static final List<String> IDEAS = List.of("emotional", "facts", "optimism");
//
//    @Override
//    public InputGuardrailResult validate(InputGuardrailRequest request) {
//        Map<String, Object> variables = request.requestParams().variables();
//        boolean anyIdea = IDEAS.stream()
//                .map(variables::get)
//                .anyMatch(LokiGroundGuardrail::hasIdea);
//        return anyIdea ? success() : fatal("Loki has nothing to remix: no god has brought an idea.");
//    }
//
//    /** Heimdall answers with a {@link FactAgentAnswer}, the other gods with an {@link AgentAnswer}. */
//    private static boolean hasIdea(Object value) {
//        if (value instanceof FactAgentAnswer facts) {
//            return !isBlank(facts.getAnswer());
//        }
//        return value instanceof AgentAnswer answer && !isBlank(answer.getAnswer());
//    }
//
//    private static boolean isBlank(String text) {
//        return text == null || text.isBlank();
//    }
//}
