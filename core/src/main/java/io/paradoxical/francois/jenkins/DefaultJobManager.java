package io.paradoxical.francois.jenkins;

import com.google.inject.Inject;
import com.squareup.okhttp.ResponseBody;
import io.paradoxical.francois.exceptions.JobCreateFailureException;
import io.paradoxical.francois.jenkins.api.JenkinsApiClient;
import org.apache.commons.io.IOUtils;
import retrofit.Response;

import javax.print.attribute.standard.JobName;
import java.io.IOException;
import java.io.InputStream;

public class DefaultJobManager {
    private final JenkinsApiClient api;

    @Inject
    public DefaultJobManager(JenkinsApiClient api) {
        this.api = api;
    }

    public void createNewTemplateJob(String jobName) throws JobCreateFailureException {
        final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("templates/freestyleDefault.xml");

        try {
            final String body = IOUtils.toString(resourceAsStream);

            final Response<ResponseBody> execute = api.createJob(jobName, body).execute();

            if (!execute.isSuccess()) {
                throw new JobCreateFailureException(execute.message());
            }
        }
        catch (IOException ex) {
            throw new JobCreateFailureException(ex);
        }
    }
}
