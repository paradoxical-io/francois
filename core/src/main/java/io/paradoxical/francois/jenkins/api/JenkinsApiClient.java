package io.paradoxical.francois.jenkins.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;
import io.paradoxical.francois.serialization.JsonMapper;
import lombok.NonNull;
import retrofit.Call;
import retrofit.Callback;
import retrofit.JacksonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import java.io.IOException;

public final class JenkinsApiClient {
    private final JenkinsApi jenkinsApi;

    private JenkinsApiClient(final JenkinsApi jenkinsApi) {
        this.jenkinsApi = jenkinsApi;
    }

    public static JenkinsApiClient createClient(String baseUrl, String userName, String password, JsonMapper jsonMapper) {
        return createClient(baseUrl,
                            userName,
                            password,
                            jsonMapper.getConfiguredMapper());
    }

    public static JenkinsApiClient createClient(String baseUrl, String userName, String password, ObjectMapper objectMapper) {

        final String basicCredential = Credentials.basic(userName, password);


        final OkHttpClient okHttpClient = new OkHttpClient();

        okHttpClient.interceptors().add(chain -> {
            final Request request = chain.request().newBuilder().header("Authorization", basicCredential).build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        JenkinsApi service = retrofit.create(JenkinsApi.class);

        return new JenkinsApiClient(service);
    }

    public Call<String> getJobConfig(String jobName) {
        return new ProxyCall<>(jenkinsApi.getJobConfigRaw(jobName), ResponseBody::string);
    }

    public Call<ResponseBody> createJob(String jobName, String configXml) {
        return jenkinsApi.createJobRaw(jobName, createXmlRequest(configXml));
    }

    public Call<ResponseBody> updateJob(String jobName, String configXml) {
        final RequestBody configXmlRequest = createXmlRequest(configXml);
        return jenkinsApi.updateJobRaw(jobName, configXmlRequest);
    }

    public Call<String> getJobPromotionConfig(String jobName, String promotionName) {
        return new ProxyCall<>(jenkinsApi.getJobPromotionConfigRaw(jobName, promotionName), ResponseBody::string);
    }

    public Call<ResponseBody> createPromotion(String jobName, String promotionName, String configXml) {
        final RequestBody configXmlRequest = createXmlRequest(configXml);
        return jenkinsApi.createPromotionRaw(jobName, promotionName, configXmlRequest);
    }

    private RequestBody createXmlRequest(final String configXml) {
        return RequestBody.create(MediaType.parse("text/xml"), configXml);
    }

    public Call<ResponseBody> getViewConfig(String viewName) {
        return jenkinsApi.getViewConfig(viewName);
    }

    public Call<ResponseBody> createView(String viewName, String configXml){
        return jenkinsApi.createView(viewName, createXmlRequest(configXml));
    }

    public Call<PromotionList> getJobPromotions(final String templateName) {
        return jenkinsApi.getJobPromotions(templateName);
    }

    public Call<JobList> getJobTemplates() {
        return jenkinsApi.getJobTemplates();
    }

    public Call<ResponseBody> addJobToView(String viewName, final String jobName) {
        return jenkinsApi.addJobToView(viewName, jobName);
    }

    public Call<JobList> getTemplatizedJobs() {
        return jenkinsApi.getTemplatizedJobs();
    }

    public Call<ResponseBody> updatePromotion(final String jobName, final String promotionName, final String promotionConfig) {
        return jenkinsApi.updatePromotionRaw(jobName, promotionName, createXmlRequest(promotionConfig));
    }

    static class ProxyCall<T> implements Call<T> {

        private final Call<ResponseBody> innerCall;
        private final IOFunction<ResponseBody, T> projection;

        public ProxyCall(
                @NonNull final Call<ResponseBody> innerCall,
                @NonNull final IOFunction<ResponseBody, T> projection) {
            this.innerCall = innerCall;
            this.projection = projection;
        }

        @Override
        public Response<T> execute() throws IOException {
            final Response<ResponseBody> response = innerCall.execute();

            return proxyResponse(response);
        }

        private Response<T> proxyResponse(final Response<ResponseBody> response) throws IOException {
            if (response.isSuccess()) {
                return Response.success(projection.apply(response.body()), response.raw());
            }

            return Response.error(response.errorBody(), response.raw());
        }

        @Override
        public void enqueue(final Callback<T> callback) {
            innerCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(final Response<ResponseBody> response, final Retrofit retrofit) {
                    try {
                        callback.onResponse(proxyResponse(response), retrofit);
                    }
                    catch (IOException e) {
                        callback.onFailure(e);
                    }
                }

                @Override
                public void onFailure(final Throwable t) {
                    callback.onFailure(t);
                }
            });
        }

        @Override
        public void cancel() {
            innerCall.cancel();
        }

        @Override
        public Call<T> clone() {
            return new ProxyCall<>(innerCall.clone(), projection);
        }

        @FunctionalInterface
        interface IOFunction<T, R> {
            R apply(T arg) throws IOException;
        }
    }

}
