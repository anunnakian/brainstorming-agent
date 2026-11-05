package io.shodo.asgard.agent;

import dev.langchain4j.agentic.declarative.PlannerAgent;
import dev.langchain4j.agentic.declarative.PlannerSupplier;
import dev.langchain4j.agentic.planner.Planner;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.V;
import io.shodo.asgard.planner.GoalOrientedPlanner;

public interface BrainStormingAgent {
      @PlannerAgent(
              outputKey = "answer",
              subAgents = {
                      OdinAgent.class,
                      FreyjaAgent.class,
                      LokiAgent.class,
                      HelAgent.class,
                      HeimdallAgent.class,
                      FreyrAgent.class})
      Result<String> answer(@V("prompt") String prompt, @V("debateId") String debateId);

      @PlannerSupplier
      static Planner planner() {
          return new GoalOrientedPlanner();
      }
  }  