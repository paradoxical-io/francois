package io.paradoxical.francois.resources.api.v1;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.inject.Inject;
import io.paradoxical.francois.jenkins.JenkinsTemplateManager;
import io.paradoxical.francois.jenkins.JobApplicationModel;
import io.paradoxical.francois.jenkins.api.JenkinsApiClient;
import io.paradoxical.francois.jenkins.api.JobList;
import io.paradoxical.francois.jenkins.templates.TemplateParameter;
import io.paradoxical.francois.model.api.v1.TemplateApplicationUpdateRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    public Response getAllTemplates() {
        try {
            final retrofit.Response<JobList> response = jenkinsApi.getJobTemplates().execute();

            if (!response.isSuccess()) {
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
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    public Response getTemplateParameters(@PathParam("templateName") String templateName) {
        try {

            final List<TemplateParameter> allParameters = templateManager.getAllParameters(templateName);

            if (allParameters == null) {
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
    @Path("/templates/{templateName}")
    @ApiOperation(value = "Create new template")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    public Response createDefaultTemplate(@PathParam("templateName") String templateName) {
        try {
            templateManager.createDefaultTemplate(templateName);

            return Response.created(URI.create(String.format("./%s", templateName))).build();
        }
        catch (Exception e) {
            logger.error(e, "Error");
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/templates/{templateName}/jobs/{jobName}")
    @ApiOperation(value = "Update job")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    public Response updateJob(
            @PathParam("templateName") String templateName,
            @PathParam("jobName") String jobName,
            TemplateApplicationUpdateRequest templateApplicationRequest) {
        try {
            if (!Objects.equals(templateApplicationRequest.getJobName(), jobName)) {
                return Response.serverError().entity(new Object() {
                    public String message = "Job name path is not equal to job name body";
                }).build();
            }
            templateManager.updateJobFromTemplate(templateApplicationRequest.getJobName(), templateName, templateApplicationRequest.getParameters());

            return Response.noContent().build();
        }
        catch (Exception e) {
            logger.error(e, "Error updating job");

            return Response.serverError().build();
        }
    }

    @GET
    @Path("/jobs")
    @ApiOperation(value = "Search jobs")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    public Response searchJobs(
            @QueryParam("search") String searchString) {
        try {
            final List<JobApplicationModel> jobApplicationModels = templateManager.listJobs(Optional.ofNullable(searchString));

            return Response.ok(jobApplicationModels).build();
        }
        catch (Exception e) {
            logger.error(e, "Error searching for jobs");

            return Response.serverError().build();
        }
    }

    @POST
    @Path("/templates/{templateName}/jobs")
    @ApiOperation(value = "Update job")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
    public Response createFromTemplate(@PathParam("templateName") String templateName, TemplateApplicationUpdateRequest templateApplicationRequest) {
        try {
            templateManager.createJobFromTemplate(templateApplicationRequest.getJobName(), templateName, templateApplicationRequest.getParameters());

            return Response.noContent().build();
        }
        catch (Exception e) {
            logger.error(e, "Error updating job");
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/templates/{templateName}/jobs")
    @ApiOperation(value = "Reapply template to all jobs")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
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
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OK") })
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
