package io.paradoxical.francois.jenkins.templates;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class JobDefinition {
    @NonNull
    JobTemplate template;

    @NonNull
    List<JobParameterValue> parameterValues;
}
