package io.shodo.asgard.agent;

import dev.langchain4j.agentic.declarative.SequenceAgent;
import dev.langchain4j.service.V;
import io.shodo.asgard.agent.model.AgentAnswer;

/** Test-only parent: hands the mortal's challenge to Freyr as 'prompt', like BrainStormingAgent will. */
public interface TestSession {

    @SequenceAgent(outputKey = "optimism", subAgents = { FreyrAgent.class })
    AgentAnswer brainstorm(@V("prompt") String prompt);
}
