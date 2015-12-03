package io.paradoxical.francois.jenkins.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class JobPromotion {
    private final String name;

    @JsonCreator
    public JobPromotion(@JsonProperty("name") final String name) {
        this.name = name;
    }
}
