package io.paradoxical.francois.jenkins.api;

import lombok.Data;

import java.util.List;

@Data
public class JobList {
    List<JobModel> jobs;
}
