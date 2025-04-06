package com.example.idpbackend.dto.template;

import lombok.Data;

import java.util.Map;

@Data
public class RepositoryRequest {
    private String organizationUrl;
    private String newRepositoryName;
    private String templateRepositoryUrl;
    private Map<String, Object> variables;
    private Map<String, Boolean> conditions;
}