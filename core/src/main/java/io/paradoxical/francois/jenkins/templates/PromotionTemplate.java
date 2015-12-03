package io.paradoxical.francois.jenkins.templates;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class PromotionTemplate extends JenkinsTemplateBase {

    private final String promotionName;

    public PromotionTemplate(final String templateName, final String promotionName, final String jobDefinition) {
        super(templateName, jobDefinition);
        this.promotionName = promotionName;
    }
}
