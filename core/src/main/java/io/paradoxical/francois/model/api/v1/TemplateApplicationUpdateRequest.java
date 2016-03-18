package io.paradoxical.francois.model.api.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.paradoxical.francois.jenkins.templates.JobParameterValue;
import lombok.Data;

import java.util.List;

@Data
public class TemplateApplicationUpdateRequest {
    private final String jobName;
    private final List<JobParameterValue> parameters;

    @JsonCreator
    public TemplateApplicationUpdateRequest(
            @JsonProperty("jobName") final String jobName,
            @JsonProperty("parameters") final List<JobParameterValue> parameters) {
        this.jobName = jobName;
        this.parameters = parameters;
    }
}
