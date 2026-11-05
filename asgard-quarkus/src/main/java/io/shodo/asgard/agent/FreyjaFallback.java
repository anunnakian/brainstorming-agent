package io.shodo.asgard.agent;

import io.shodo.asgard.agent.model.AgentAnswer;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

/** What Freyja says once her retries are spent: the debate goes on, without any invented idea. */
public class FreyjaFallback implements FallbackHandler<AgentAnswer> {

    static final String SILENCE = "My heart has no words for this challenge right now. Go on without me.";

    @Override
    public AgentAnswer handle(ExecutionContext context) {
        AgentAnswer answer = new AgentAnswer(SILENCE);
        answer.setName("Freyja");
        return answer;
    }
}
