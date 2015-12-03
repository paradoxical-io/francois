package io.paradoxical.francois.configurations;

import lombok.Data;

@Data
public class JenkinsConfiguration {
    private String url;
    private String user;
    private String token;

    public String getUrl() {
        return System.getenv().getOrDefault("JENKINS_URL", url);
    }

    public String getUser() {
        return System.getenv().getOrDefault("JENKINS_USER", user);
    }

    public String getToken() {
        return System.getenv().getOrDefault("JENKINS_TOKEN", token);
    }
}
