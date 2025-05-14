package com.example.idpbackend.dto.jenkins;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePipelineRequest {
    private String jobName;
    private String repoUrl;
    private Map<String, Object> variables;
    private String jenkinsfilePath;

}
