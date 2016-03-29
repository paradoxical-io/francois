package io.paradoxical.francois.resources;

import io.dropwizard.views.View;
import io.paradoxical.francois.ServiceConfiguration;
import lombok.Getter;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class PagesResource {
    private final ServiceConfiguration configuration;

    @Inject
    public PagesResource(ServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    @GET
    public FrancoisView francois() {
        return new FrancoisView("/francois.mustache", configuration.getJenkinsConfiguration().getUrl());
    }

    public static class FrancoisView extends View {
        @Getter
        private final String jenkinsUrl;

        protected FrancoisView(final String templateName, final String jenkinsUrl) {
            super(templateName);
            this.jenkinsUrl = jenkinsUrl;
        }
    }
}