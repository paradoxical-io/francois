package io.paradoxical.francois.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import io.paradoxical.francois.ServiceConfiguration;
import io.paradoxical.francois.configurations.JenkinsConfiguration;
import io.paradoxical.francois.jenkins.api.JenkinsApiClient;
import io.paradoxical.francois.serialization.JsonMapper;

public class JenkinsApiModule extends AbstractModule {

    @Override protected void configure() {
    }

    @Provides
    public JenkinsApiClient getJenkinsApiClient(ServiceConfiguration configuration, JsonMapper jsonMapper) {
        final JenkinsConfiguration jenkinsConfiguration = configuration.getJenkinsConfiguration();
        return JenkinsApiClient.createClient(
                jenkinsConfiguration.getUrl(),
                jenkinsConfiguration.getUser(),
                jenkinsConfiguration.getToken(),
                jsonMapper.getConfiguredMapper());
    }
}
