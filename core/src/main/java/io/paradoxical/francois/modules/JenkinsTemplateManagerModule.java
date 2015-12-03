package io.paradoxical.francois.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.paradoxical.francois.ServiceConfiguration;
import io.paradoxical.francois.jenkins.JenkinsTemplateManager;
import io.paradoxical.francois.jenkins.TemplateManager;
import io.paradoxical.francois.jenkins.api.JenkinsApiClient;

public class JenkinsTemplateManagerModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    public JenkinsTemplateManager getJenkinsApi(JenkinsApiClient jenkinsApi, ServiceConfiguration configuration) {
        return new TemplateManager(jenkinsApi);
    }
}
