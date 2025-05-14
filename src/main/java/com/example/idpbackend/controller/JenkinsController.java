package com.example.idpbackend.controller;

import com.example.idpbackend.dto.jenkins.CreatePipelineRequest;
import com.example.idpbackend.service.JenkinsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jenkins")
public class JenkinsController {

    private final JenkinsService jenkinsService;

    public JenkinsController(JenkinsService jenkinsService) {
        this.jenkinsService = jenkinsService;
    }

    @PostMapping("/create-pipeline")
    public ResponseEntity<String> createPipeline(@RequestBody CreatePipelineRequest request) {
        jenkinsService.createOrUpdateJenkinsJobAndPipeline(request);

        return ResponseEntity.ok("Jenkins job created/updated and pipeline info saved successfully!");
    }

    @PostMapping("/trigger-pipeline")
    public ResponseEntity<String> triggerPipeline(@RequestParam String jobName) {
        jenkinsService.triggerJob(jobName);
        return ResponseEntity.ok("Jenkins job triggered!");
    }

    @GetMapping("/pipeline-status")
    public ResponseEntity<String> getPipelineStatus(@RequestParam String jobName) {
        String status = jenkinsService.getJobStatus(jobName);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
