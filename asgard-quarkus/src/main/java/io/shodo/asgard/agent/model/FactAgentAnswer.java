package io.shodo.asgard.agent.model;

import java.util.List;

public class FactAgentAnswer {

    private String name;
    private String answer;
    private List<String> sources;

    public FactAgentAnswer() {
    }

    public FactAgentAnswer(String answer) {
        this.answer = answer;
    }

    public FactAgentAnswer(String name, String answer, List<String> sources) {
        this.name = name;
        this.answer = answer;
        this.sources = sources;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "AgentAnswer{" +
                "name='" + name + '\'' +
                ", answer='" + answer + '\'' +
                ", sources=" + sources +
                '}';
    }
}
