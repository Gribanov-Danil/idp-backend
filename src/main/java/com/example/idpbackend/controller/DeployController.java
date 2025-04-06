package com.example.idpbackend.controller;

import com.example.idpbackend.service.DeployService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/deploy")
public class DeployController {

    @Autowired
    private final DeployService deployService;

    public DeployController(DeployService deployService) {
        this.deployService = deployService;
    }

    @PostMapping("/frontend")
    public ResponseEntity<String> deployFrontend(
            @RequestParam int port,
            @RequestParam String jobName
    ) throws IOException {
        // Загружаем build.zip из Jenkins (можно через JenkinsService)
        String artifactUrl = "http://localhost:9090/job/" + jobName + "/lastSuccessfulBuild/artifact/build.zip";
        Path tempZip = Paths.get("temp-" + jobName + ".zip");

        try (InputStream in = new URL(artifactUrl).openStream()) {
            Files.copy(in, tempZip, StandardCopyOption.REPLACE_EXISTING);
        }

        deployService.deployFrontend(tempZip, port);
        return ResponseEntity.ok("Frontend развернут на порту: " + port);
    }
}
