package io.paradoxical.francois.resources;

import io.dropwizard.views.View;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class PagesResource {

    @GET
    @Path("/swagger")
    public IndexView swagger() {
        return new IndexView("/swagger.mustache");
    }

    @GET
    public IndexView francois() {
        return new IndexView("/francois.mustache");
    }


    public static class IndexView extends View {
        protected IndexView(String templateName) {
            super(templateName);
        }
    }
}