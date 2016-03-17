package io.paradoxical.francois.jenkins;

import io.paradoxical.francois.jenkins.api.JenkinsApiClient;
import io.paradoxical.francois.jenkins.api.JobPromotion;
import io.paradoxical.francois.jenkins.api.PromotionList;
import io.paradoxical.francois.jenkins.templates.PromotionTemplate;
import javaslang.control.Try;
import retrofit.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PromotionTemplateLoader {

    private final JenkinsApiClient jenkinsClient;

    public PromotionTemplateLoader(final JenkinsApiClient jenkinsClient) {
        this.jenkinsClient = jenkinsClient;
    }

    public List<PromotionTemplate> getPromotionTemplates(String templateName) throws IOException {
        return getJobPromotions(templateName).stream()
                                             .map(promotion -> Try.of(() -> getPromotionTemplate(templateName, promotion)))
                                             .map(tryGetPromotion -> tryGetPromotion.get())
                                             .collect(toList());
    }

    private PromotionTemplate getPromotionTemplate(final String templateName, final JobPromotion promotion) throws IOException {
        final Response<String> promotionConfigResponse = jenkinsClient.getJobPromotionConfig(templateName, promotion.getName()).execute();

        return new PromotionTemplate(templateName, promotion.getName(), promotionConfigResponse.body());
    }

    private List<JobPromotion> getJobPromotions(final String templateName) throws IOException {
        final Response<PromotionList> promotionListResponse = jenkinsClient.getJobPromotions(templateName).execute();

        if (promotionListResponse.isSuccess()) {
            return promotionListResponse.body().getPromotions();
        }

        return Collections.emptyList();
    }
}
