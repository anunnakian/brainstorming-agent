package io.shodo.asgard.agent;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;


public enum God {
    ODIN("blue"),
    HEIMDALL("white"),
    LOKI("green"),
    FREYR("yellow"),
    HEL("black"),
    FREYJA("red");

    private final String hat;

    God(String hat) {
        this.hat = hat;
    }

    public String hat() {
        return hat;
    }

    @JsonValue
    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    /** The god an agent speaks for: each agent is named after its god in {@code @Agent(name = ...)}. */
    public static Optional<God> ofAgent(String agentName) {
        return Arrays.stream(values()).filter(god -> god.name().equalsIgnoreCase(agentName)).findFirst();
    }
}
