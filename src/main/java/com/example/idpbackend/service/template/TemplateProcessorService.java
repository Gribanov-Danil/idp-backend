package com.example.idpbackend.service.template;

import com.example.idpbackend.dto.template.RepositoryRequest;

public interface TemplateProcessorService {
    String processAndCreateRepository(RepositoryRequest request) throws Exception;
}
