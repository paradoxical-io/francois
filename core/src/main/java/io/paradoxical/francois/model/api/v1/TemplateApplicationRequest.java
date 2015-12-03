package io.paradoxical.francois.model.api.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.paradoxical.francois.jenkins.templates.JobParameterValue;
import lombok.Data;

import java.util.List;

@Data
public class TemplateApplicationRequest {
    private final String newJobName;
    private final List<JobParameterValue> parameters;

    @JsonCreator
    public TemplateApplicationRequest(
            @JsonProperty("newJobName") final String newJobName,
            @JsonProperty("parameters") final List<JobParameterValue> parameters) {
        this.newJobName = newJobName;
        this.parameters = parameters;
    }
}
