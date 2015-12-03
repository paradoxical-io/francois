package io.paradoxical.francois.jenkins.templates;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.NonNull;
import lombok.Value;

@Value
public class TemplateParameter {
    @NonNull
    private final String name;

    @NonNull
    private final String defaultValue;

    @JsonCreator
    public TemplateParameter(final String name, final String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }
}
