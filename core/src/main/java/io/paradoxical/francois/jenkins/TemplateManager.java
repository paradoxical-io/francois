package io.paradoxical.francois.jenkins;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.squareup.okhttp.ResponseBody;
import io.paradoxical.francois.exceptions.JobCreateFailureException;
import io.paradoxical.francois.jenkins.api.ApiConstants;
import io.paradoxical.francois.jenkins.api.JenkinsApiClient;
import io.paradoxical.francois.jenkins.api.JobList;
import io.paradoxical.francois.jenkins.templates.JobParameterValue;
import io.paradoxical.francois.jenkins.templates.JobTemplate;
import io.paradoxical.francois.jenkins.templates.JobTemplateApplicationConfig;
import io.paradoxical.francois.jenkins.templates.PromotionTemplate;
import io.paradoxical.francois.jenkins.templates.TemplateParameter;
import javaslang.Function2;
import javaslang.control.Try;
import retrofit.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class TemplateManager implements JenkinsTemplateManager {
    private static final String TEMPLATE_SAVE_SLOT = "{$ Francois.Template.Params $}";

    private final JenkinsApiClient jenkinsClient;
    private final DefaultJobManager defaultJobManager;
    private final TemplateParameterResolver templateParameterResolver;
    private final PromotionTemplateLoader promotionTemplateLoader;

    @Inject
    public TemplateManager(JenkinsApiClient jenkinsClient, DefaultJobManager defaultJobManager) {
        this.jenkinsClient = jenkinsClient;
        this.defaultJobManager = defaultJobManager;
        templateParameterResolver = new TemplateParameterResolver();
        promotionTemplateLoader = new PromotionTemplateLoader(jenkinsClient);
    }

    @Override
    public List<TemplateParameter> getAllParameters(String templateName) throws IOException {

        final JobTemplate jobTemplate = getJobTemplate(templateName);

        final List<PromotionTemplate> promotions = promotionTemplateLoader.getPromotionTemplates(templateName);

        return getLinearizedParameters(templateParameterResolver.resolveDefaultParameters(jobTemplate, promotions));
    }

    private List<TemplateParameter> getLinearizedParameters(HashMap<String, String> parametersMap) {
        return parametersMap.entrySet()
                            .stream()
                            .map(e -> new TemplateParameter(e.getKey(), Strings.nullToEmpty(e.getValue())))
                            .sorted((f, s) -> f.getName().compareTo(s.getName()))
                            .collect(toList());
    }

    @Override
    public JobTemplate getJobTemplate(final String jobTemplateName) throws IOException {
        final Response<String> response = jenkinsClient.getJobConfig(jobTemplateName).execute();
        return new JobTemplate(jobTemplateName, response.body());
    }

    @Override
    public List<JobApplicationModel> getTemplatizedJobs(final String templateName) throws Exception {
        final Response<JobList> response = jenkinsClient.getTemplatizedJobs().execute();

        if (response.isSuccess()) {

            final List<JobApplicationModel> jobModels = response.body().getJobs().stream().map(jobModel -> {
                final JobTemplateApplicationConfig config = loadTemplateConfig(jobModel.getDescription());

                return new JobApplicationModel(jobModel.getName(), config);
            }).collect(toList());

            return jobModels.stream().filter(jobModel -> isInstanceOfTemplate(templateName, jobModel)).collect(toList());
        }

        return Collections.emptyList();
    }

    @Override
    public void updateJobFromTemplate(final String jobName, final String templateName, final List<JobParameterValue> parameterValues) throws Exception {
        final JobTemplate jobTemplate = getJobTemplate(templateName);

        final List<PromotionTemplate> promotions = promotionTemplateLoader.getPromotionTemplates(templateName);

        final JobApplicationModel jobApplicationModel = new JobApplicationModel(jobName, new JobTemplateApplicationConfig(templateName, parameterValues));

        updateJobFromTemplate(jobTemplate, promotions, jobApplicationModel);
    }

    @Override
    public void createJobFromTemplate(
            final String newJobName,
            final String templateName,
            final List<JobParameterValue> parameterValues) throws Exception {

        final JobTemplate jobTemplate = getJobTemplate(templateName);

        final List<PromotionTemplate> promotions = promotionTemplateLoader.getPromotionTemplates(templateName);

        templatize(parameterValues, jobTemplate, promotions,
                   jobConfig -> Try.run(() -> {
                       final Response<ResponseBody> createJobResponse =
                               jenkinsClient.createJob(newJobName, saveJobParameters(templateName, jobConfig, parameterValues))
                                            .execute();

                       if (!createJobResponse.isSuccess()) {
                           final String message = String.format("Failed to create job '%s'", newJobName);
                           throw new RuntimeException(message);
                       }

                       final Response<ResponseBody> addToViewResponse = jenkinsClient.addJobToView(ApiConstants.TemplatizedViewName, newJobName).execute();

                       if (!addToViewResponse.isSuccess()) {
                           final String message = String.format("Failed to add created job '%s' to templatized job list view", newJobName);
                           throw new RuntimeException(message);
                       }
                   }),
                   (promotion, promotionConfig) -> Try.run(() -> {
                       final Response<ResponseBody> promotionCreateResponse = jenkinsClient.createPromotion(newJobName, promotion.getPromotionName(), promotionConfig)
                                                                                           .execute();

                       if (!promotionCreateResponse.isSuccess()) {
                           final String message = String.format("Failed to create promotion '%s' on Job '%s'", promotion.getPromotionName(), newJobName);
                           throw new RuntimeException(message);
                       }
                   }));
    }

    @Override
    public void reapplyTemplate(final String templateName) throws Exception {
        final List<JobApplicationModel> templatizedJobs = getTemplatizedJobs(templateName);

        final JobTemplate jobTemplate = getJobTemplate(templateName);

        final List<PromotionTemplate> promotions = promotionTemplateLoader.getPromotionTemplates(templateName);

        templatizedJobs.stream()
                       .forEach(job -> Try.run(() -> updateJobFromTemplate(jobTemplate, promotions, job))
                                          .orElseThrow(e -> new RuntimeException(String.format("Error updating job '%s'", job.getJobName()), e)));
    }

    @Override
    public void createDefaultTemplate(final String templateName) throws JobCreateFailureException {
        defaultJobManager.createNewTemplateJob(templateName);
    }

    private void updateJobFromTemplate(
            final JobTemplate jobTemplate,
            final List<PromotionTemplate> promotions,
            final JobApplicationModel job) throws Exception {

        final String jobName = job.getJobName();

        final List<JobParameterValue> suppliedParameters = job.getConfig().getParameters();

        templatize(suppliedParameters, jobTemplate, promotions,
                   jobConfig -> Try.run(() -> {
                       final Response<ResponseBody> createJobResponse;

                       createJobResponse = jenkinsClient.updateJob(jobName, jobConfig)
                                                        .execute();

                       if (!createJobResponse.isSuccess()) {
                           throw new RuntimeException(String.format("Failed to update job '%s'", jobName));
                       }
                   }),
                   (promotion, promotionConfig) -> Try.run(() -> {
                       final Response<ResponseBody> promotionCreateResponse =
                               jenkinsClient.updatePromotion(jobName, promotion.getPromotionName(), promotionConfig)
                                            .execute();

                       if (!promotionCreateResponse.isSuccess()) {
                           throw new RuntimeException(String.format("Failed to update promotion '%s' on Job '%s'", promotion.getPromotionName(), jobName));
                       }
                   }));
    }

    private String saveJobParameters(final String templateName, final String configXml, final List<JobParameterValue> parameterValues) throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);
        return configXml.replace(TEMPLATE_SAVE_SLOT, objectMapper.writeValueAsString(new JobTemplateApplicationConfig(templateName, parameterValues)));
    }

    private void templatize(
            List<JobParameterValue> suppliedParameters,
            final JobTemplate jobTemplate,
            final List<PromotionTemplate> promotions,
            Function<String, Try<Void>> jobConfigHandler,
            Function2<PromotionTemplate, String, Try<Void>> promotionConfigHandler) throws Exception {

        final HashMap<String, String> resolvedParameters =
                templateParameterResolver.fullyResolveTemplateApplicationParameters(suppliedParameters, jobTemplate, promotions);


        final String jobConfig =
                saveJobParameters(jobTemplate.getTemplateName(), jobTemplate.apply(resolvedParameters), suppliedParameters);

        jobConfigHandler.apply(jobConfig).orElseThrow(e -> new RuntimeException("Error handling job config", e));

        for (PromotionTemplate promotion : promotions) {
            final String promotionConfig = promotion.apply(resolvedParameters);

            promotionConfigHandler.apply(promotion, promotionConfig).orElseThrow(e -> new RuntimeException("Error handling promotion", e));


        }
    }

    private boolean isInstanceOfTemplate(final String templateName, final JobApplicationModel jobModel) {
        final JobTemplateApplicationConfig templateConfig = jobModel.getConfig();

        if (templateConfig == null) {
            return false;
        }

        return templateConfig.getTemplateName()
                             .equalsIgnoreCase(templateName);
    }

    private JobTemplateApplicationConfig loadTemplateConfig(final String description) {
        try {
            return new ObjectMapper().readValue(description.replace("<pre>", "").replace("</pre>", ""), JobTemplateApplicationConfig.class);
        }
        catch (IOException e) {
            return null;
        }
    }
}
