package io.paradoxical.francois.jenkins.templates;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.Value;

@Value
public class JobParameterValue {
    @NonNull
    private final String name;

    @NonNull
    private final String value;

    public static JobParameterValue create(TemplateParameter templateParameter, String value){
        return new JobParameterValue(templateParameter.getName(), value == null ? templateParameter.getDefaultValue() : value);
    }

    @JsonCreator
    public static JobParameterValue create(
            @JsonProperty("name") String name,
            @JsonProperty("value") String value){
        return new JobParameterValue(name, value);
    }
}
