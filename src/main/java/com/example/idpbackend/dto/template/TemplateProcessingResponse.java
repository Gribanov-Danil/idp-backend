package com.example.idpbackend.dto.template;

import lombok.Data;

import java.util.List;

@Data
public class TemplateProcessingResponse {
    private boolean success;
    private String message;
    private String repositoryUrl;
    private List<String> warnings;

    public TemplateProcessingResponse(boolean success, String message, String repositoryUrl) {
        this.success = success;
        this.message = message;
        this.repositoryUrl = repositoryUrl;
    }

    public TemplateProcessingResponse(boolean success, String message, String repositoryUrl, List<String> warnings) {
        this.success = success;
        this.message = message;
        this.repositoryUrl = repositoryUrl;
        this.warnings = warnings;
    }

}
