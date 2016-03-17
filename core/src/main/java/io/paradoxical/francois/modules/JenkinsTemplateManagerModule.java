package io.paradoxical.francois.modules;

import com.google.inject.AbstractModule;
import io.paradoxical.francois.jenkins.JenkinsTemplateManager;
import io.paradoxical.francois.jenkins.TemplateManager;

public class JenkinsTemplateManagerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(JenkinsTemplateManager.class).to(TemplateManager.class);
    }
}
