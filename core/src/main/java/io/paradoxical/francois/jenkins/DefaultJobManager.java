package io.paradoxical.francois.jenkins;

import com.godaddy.logging.Logger;
import com.google.inject.Inject;
import com.squareup.okhttp.ResponseBody;
import io.paradoxical.francois.exceptions.JobCreateFailureException;
import io.paradoxical.francois.jenkins.api.ApiConstants;
import io.paradoxical.francois.jenkins.api.JenkinsApiClient;
import org.apache.commons.io.IOUtils;
import retrofit.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static com.godaddy.logging.LoggerFactory.getLogger;

public class DefaultJobManager {
    private static final Logger logger = getLogger(DefaultJobManager.class);

    private final JenkinsApiClient api;

    @Inject
    public DefaultJobManager(JenkinsApiClient api) {
        this.api = api;
    }

    public void createNewTemplateJob(String jobName) throws JobCreateFailureException {
        try {
            validateViewsExist();

            createJobTemplate(jobName);

            addToTemplatesList(jobName);
        }
        catch (IOException ex) {
            throw new JobCreateFailureException(ex);
        }
    }

    private void addToTemplatesList(final String jobName) throws IOException {
        api.addJobToView(ApiConstants.TemplateVewName, jobName).execute();
    }

    private void createJobTemplate(final String jobName) throws JobCreateFailureException, IOException {
        String body = getTemplate("freestyleDefault.xml");

        final Response<ResponseBody> execute = api.createJob(jobName, body).execute();

        if (!execute.isSuccess()) {
            throw new JobCreateFailureException(execute.message());
        }
    }

    private String getTemplate(final String resourceName) throws IOException {
        final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("templates/" + resourceName);

        return IOUtils.toString(resourceAsStream);
    }

    private void validateViewsExist() throws IOException, JobCreateFailureException {
        for (String view : Arrays.asList(ApiConstants.TemplateVewName, ApiConstants.TemplatizedViewName)) {
            final Response<ResponseBody> getResponse = api.getViewConfig(view).execute();

            if (!getResponse.isSuccess()) {
                String body = getTemplate("templateViewDefault.xml");

                final Response<ResponseBody> createTemplateViewResponse = api.createView(view, body).execute();

                if (!createTemplateViewResponse.isSuccess()) {
                    logger.warn("Couldn't create view %s", view);
                }
            }
        }
    }
}
