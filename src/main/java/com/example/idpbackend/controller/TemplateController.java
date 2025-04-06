package com.example.idpbackend.controller;

import com.example.idpbackend.dto.template.RepositoryRequest;
import com.example.idpbackend.dto.template.TemplateProcessingResponse;
import com.example.idpbackend.service.template.TemplateProcessorService;
import com.example.idpbackend.service.template.TemplateProcessorServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateProcessorService templateProcessorService;
    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);

    public TemplateController(TemplateProcessorService templateProcessorService) {
        this.templateProcessorService = templateProcessorService;
    }

    @PostMapping("/process")
    public TemplateProcessingResponse processTemplate(@RequestBody RepositoryRequest request) {
        try {
            String repositoryUrl = templateProcessorService.processAndCreateRepository(request);
            return new TemplateProcessingResponse(
                    true,
                    "Repository created successfully",
                    repositoryUrl,
                    ((TemplateProcessorServiceImpl) templateProcessorService).getWarnings() // если нужно
            );
        } catch (Exception e) {
            return new TemplateProcessingResponse(false, "Error processing template: " + e.getMessage(), null);
        }
    }
}
