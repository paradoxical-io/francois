package io.paradoxical.francois.jenkins;

import io.paradoxical.francois.jenkins.templates.JobTemplateApplicationConfig;
import lombok.Value;

@Value
public class JobApplicationModel {
    private final String jobName;
    private final JobTemplateApplicationConfig config;
}
