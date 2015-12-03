package io.paradoxical.francois.healthChecks;

import com.google.inject.Provider;
import com.hubspot.dropwizard.guice.InjectableHealthCheck;
import io.paradoxical.francois.ServiceConfiguration;
import io.paradoxical.francois.jenkins.api.JenkinsApiClient;
import io.paradoxical.francois.jenkins.api.JobList;
import retrofit.Response;

@SuppressWarnings("unused")
public class JenkinsHealthCheck extends InjectableHealthCheck {
    private final ServiceConfiguration configuration;
    private final Provider<JenkinsApiClient> clientProvider;

    public JenkinsHealthCheck(ServiceConfiguration configuration, Provider<JenkinsApiClient> clientProvider) {
        this.configuration = configuration;
        this.clientProvider = clientProvider;
    }

    @Override
    public String getName() {
        return "jenkins-status";
    }

    @Override
    protected Result check() throws Exception {
        try {

            final JenkinsApiClient jenkinsApiClient = clientProvider.get();

            final Response<JobList> jobListResponse = jenkinsApiClient.getJobTemplates().execute();

            return Result.healthy("Connected to jenkins at: %s Job Templates Count: %s",
                                  configuration.getJenkinsConfiguration().getUrl(),
                                  jobListResponse.body().getJobs().size());
        }
        catch (Exception ex) {
            return Result.unhealthy("Unable to connect to jenkins at: %s", configuration.getJenkinsConfiguration().getUrl());
        }
    }
}
