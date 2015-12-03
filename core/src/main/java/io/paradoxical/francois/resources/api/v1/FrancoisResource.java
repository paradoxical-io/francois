package io.paradoxical.francois.resources.api.v1;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import io.paradoxical.francois.jenkins.JenkinsTemplateManager;
import io.paradoxical.francois.jenkins.JobApplicationModel;
import io.paradoxical.francois.jenkins.api.JenkinsApiClient;
import io.paradoxical.francois.jenkins.api.JobList;
import io.paradoxical.francois.jenkins.templates.TemplateParameter;
import io.paradoxical.francois.model.api.v1.TemplateApplicationRequest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@Path("api/v1/francois")
@Api(value = "api/v1/francois", description = "Francois api")
@Produces(MediaType.APPLICATION_JSON)
public class FrancoisResource {

    private static final Logger logger = LoggerFactory.getLogger(FrancoisResource.class);
    private final JenkinsApiClient jenkinsApi;
    private final JenkinsTemplateManager templateManager;


    @Inject
    public FrancoisResource(JenkinsApiClient jenkinsApi, JenkinsTemplateManager templateManager) {
        this.jenkinsApi = jenkinsApi;
        this.templateManager = templateManager;
    }

    @GET
    @Path("/templates")
    @ApiOperation(value = "Get all templates")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    public Response getAllTemplates() {
        try {
            final retrofit.Response<JobList> response = jenkinsApi.getJobTemplates().execute();

            if(!response.isSuccess()){
                return Response.serverError().build();
            }

            return Response.ok(response.body().getJobs()).build();
        }
        catch (IOException e) {
            logger.error(e, "Error");
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/templates/{templateName}/parameters")
    @ApiOperation(value = "Get template parameters")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    public Response getTemplateParameters(@PathParam("templateName") String templateName) {
        try {

            final List<TemplateParameter> allParameters = templateManager.getAllParameters(templateName);

            if(allParameters == null){
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return Response.ok(allParameters).build();
        }
        catch (IOException e) {
            logger.error(e, "Error");
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/templates/{templateName}/jobs")
    @ApiOperation(value = "Create job")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    public Response createFromTemplate(@PathParam("templateName") String templateName, TemplateApplicationRequest templateApplicationRequest) {
        try {
            templateManager.createJobFromTemplate(templateApplicationRequest.getNewJobName(), templateName, templateApplicationRequest.getParameters());

            return Response.created(URI.create(String.format("./%s", templateApplicationRequest.getNewJobName()))).build();
        }
        catch (Exception e) {
            logger.error(e, "Error");
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/templates/{templateName}/jobs")
    @ApiOperation(value = "Reapply template to all jobs")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    public Response reapplyTemplate(@PathParam("templateName") String templateName) {
        try {
            templateManager.reapplyTemplate(templateName);

            return Response.noContent().build();
        }
        catch (Exception e) {
            logger.error(e, "Error");
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/templates/{templateName}/jobs")
    @ApiOperation(value = "Get templatized jobs")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK")})
    public Response getTemplatizedJobs(@PathParam("templateName") String templateName) {
        try {
            final List<JobApplicationModel> templatizedJobs = templateManager.getTemplatizedJobs(templateName);

            return Response.ok(templatizedJobs).build();
        }
        catch (Exception e) {
            logger.error(e, "Error");
            return Response.serverError().build();
        }
    }

}
