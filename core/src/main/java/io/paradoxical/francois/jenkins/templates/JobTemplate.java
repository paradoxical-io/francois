package io.paradoxical.francois.jenkins.templates;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class JobTemplate extends JenkinsTemplateBase {

    public JobTemplate(final String templateName, final String jobDefinition) {
        super(templateName, jobDefinition);
    }
}
