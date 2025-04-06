package com.example.idpbackend.dto.jenkins;

import lombok.Data;

import java.util.Map;

@Data
public class CreatePipelineRequest {
    private String jobName;
    private String repoUrl;
    private Map<String, Object> variables;
    private String jenkinsfilePath;

}
