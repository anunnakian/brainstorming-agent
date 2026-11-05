package io.shodo.asgard.agent.model;

public class AgentAnswer {

    private String name;
    private String answer;

    public AgentAnswer() {
    }

    public AgentAnswer(String answer) {
        this.answer = answer;
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

    @Override
    public String toString() {
        return "AgentAnswer{" +
                "name='" + name + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
