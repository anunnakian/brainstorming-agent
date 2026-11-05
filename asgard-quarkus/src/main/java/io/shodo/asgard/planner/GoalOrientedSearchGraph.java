package io.shodo.asgard.planner;

import dev.langchain4j.agentic.planner.AgentArgument;
import dev.langchain4j.agentic.planner.AgentInstance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoalOrientedSearchGraph {

    private static final Logger LOG = LoggerFactory.getLogger(GoalOrientedSearchGraph.class);

    private record NodePair(DependencyGraphSearch.Node input, DependencyGraphSearch.Node output) {}

    private final Map<String, DependencyGraphSearch.Node> nodes = new HashMap<>();
    private final Map<NodePair, AgentInstance> edges = new HashMap<>();

    public GoalOrientedSearchGraph(List<AgentInstance> agents) {
        init(agents);
    }

    private void init(List<AgentInstance> agents) {
        for (AgentInstance agent : agents) {
            List<DependencyGraphSearch.Node> inputs = agent.arguments().stream()
                    .map(AgentArgument::name)
                    .map(arg -> nodes.computeIfAbsent(arg, DependencyGraphSearch.Node::new))
                    .toList();
            DependencyGraphSearch.Node output = nodes.computeIfAbsent(agent.outputKey(), DependencyGraphSearch.Node::new);

            inputs.forEach(input -> {
                input.addOutput(output);
                edges.put(new NodePair(input, output), agent);
            });
        }
    }

    public List<AgentInstance> search(Collection<String> preconditions, String goal) {
        DependencyGraphSearch.Node goalNode = nodes.get(goal);
        if (goalNode == null) {
            // no agent produces the goal
            return List.of();
        }
        List<DependencyGraphSearch.Node> nodesPath = DependencyGraphSearch.search(
                goalNode,
                preconditions.stream().map(nodes::get).filter(Objects::nonNull).toList());

        if (nodesPath == null) {
            return List.of();
        }

        int preconditionsCounter = 0;
        List<AgentInstance> agentsPath = new ArrayList<>();
        for (int i = 1; i < nodesPath.size(); i++) {
            DependencyGraphSearch.Node output = nodesPath.get(i);
            if (preconditions.contains(output.getId())) {
                preconditionsCounter++;
                continue;
            }
            for (int j = i - 1; j >= 0; j--) {
                AgentInstance agent = edges.get(new NodePair(nodesPath.get(j), output));
                if (agent != null) {
                    agentsPath.add(agent);
                    break;
                }
            }
            if (agentsPath.size() != i - preconditionsCounter) {
                throw new IllegalStateException("No path found for DependencyGraphSearch.Node: " + output.getId());
            }
        }

        LOG.info(
                "Agents path sequence: {}",
                agentsPath.stream().map(AgentInstance::name).toList());

        return agentsPath;
    }
}