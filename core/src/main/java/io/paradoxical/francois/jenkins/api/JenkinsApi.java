package io.paradoxical.francois.jenkins.api;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface JenkinsApi {
    @GET("/view/" + ApiConstants.TemplateVewName + "/api/json?tree=jobs[name,description]")
    Call<JobList> getJobTemplates();

    @POST("/view/" + ApiConstants.TemplatizedViewName + "/api/json?tree=jobs[name,description]")
    Call<JobList> getTemplatizedJobs();

    @GET("/job/api/json?tree=jobs[name,description]")
    Call<JobList> getAllJobs();

    @GET("/view/" + ApiConstants.TemplateVewName + "/job/{jobName}/config.xml")
    Call<ResponseBody> getJobConfigRaw(@Path("jobName") String jobName);

    @GET("/view/" + ApiConstants.TemplateVewName + "/job/{jobName}/api/json?tree=description,name")
    Call<JobModel> getJobJson(@Path("jobName") String jobName);

    @POST("/createItem")
    Call<ResponseBody> createJobRaw(@Query("name") String jobName, @Body RequestBody configXml);

    @POST("/job/{jobName}/config.xml")
    Call<ResponseBody> updateJobRaw(@Path("jobName") String jobName, @Body RequestBody configXml);

    @GET("/view/{name}/config.xml")
    Call<ResponseBody> getViewConfig(@Path("name") String name);

    @POST("/createView")
    Call<ResponseBody> createView(@Query("name") String name, @Body RequestBody configXml);

    @GET("/job/{jobName}/promotion/api/json?tree=processes[name]")
    Call<PromotionList> getJobPromotions(@Path("jobName") String jobName);

    @GET("/job/{jobName}/promotion/process/{promotionName}/config.xml")
    Call<ResponseBody> getJobPromotionConfigRaw(
            @Path("jobName") String jobName,
            @Path("promotionName") String promotionName);

    @POST("/job/{jobName}/promotion/createProcess")
    Call<ResponseBody> createPromotionRaw(
            @Path("jobName") String jobName,
            @Query("name") String promotionName,
            @Body RequestBody configXml);

    @POST("/job/{jobName}/promotion/process/{promotionName}/config.xml")
    Call<ResponseBody> updatePromotionRaw(
            @Path("jobName") String jobName,
            @Path("promotionName") String promotionName,
            @Body RequestBody configXml);

    @POST("/view/{viewName}/addJobToView")
    Call<ResponseBody> addJobToView(@Path("viewName") String viewName, @Query("name") String jobName);
}

