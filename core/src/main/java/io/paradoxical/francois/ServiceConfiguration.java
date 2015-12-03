package io.paradoxical.francois;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.paradoxical.francois.configurations.JenkinsConfiguration;
import io.paradoxical.francois.configurations.JerseyConfiguration;
import io.dropwizard.Configuration;
import lombok.Setter;

public class ServiceConfiguration extends Configuration {

    @Setter
    @JsonProperty("jersey")
    private JerseyConfiguration jerseyConfiguration;

    @Setter
    @JsonProperty("jenkins")
    private JenkinsConfiguration jenkinsConfiguration;

    public JenkinsConfiguration getJenkinsConfiguration() {
        if (jenkinsConfiguration == null) {
            return new JenkinsConfiguration();
        }

        return jenkinsConfiguration;
    }

    public JerseyConfiguration getJerseyConfiguration() {
        if (jerseyConfiguration == null) {
            return new JerseyConfiguration();
        }

        return jerseyConfiguration;
    }
}
