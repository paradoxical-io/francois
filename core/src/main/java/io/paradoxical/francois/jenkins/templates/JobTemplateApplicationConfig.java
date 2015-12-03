package io.paradoxical.francois.jenkins.templates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class JobTemplateApplicationConfig {
    private final String templateName;
    private final List<JobParameterValue> parameters;

    @JsonCreator
    public JobTemplateApplicationConfig(
            @JsonProperty("templateName") final String templateName,
            @JsonProperty("parameters") final List<JobParameterValue> parameters) {
        this.templateName = templateName;
        this.parameters = parameters;
    }
}
