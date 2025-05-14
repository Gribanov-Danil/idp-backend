package com.example.idpbackend.controller;

import com.example.idpbackend.entity.Deploy;
import com.example.idpbackend.service.DeployService;
import com.example.idpbackend.service.ArtifactDownloaderService;
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

    private final DeployService deployService;
    private final ArtifactDownloaderService artifactDownloaderService;

    @Autowired
    public DeployController(DeployService deployService, ArtifactDownloaderService artifactDownloaderService) {
        this.deployService = deployService;
        this.artifactDownloaderService = artifactDownloaderService;
    }

    @PostMapping("/frontend")
    public ResponseEntity<String> deployFrontend(
            @RequestParam int port,
            @RequestParam String jobName,
            @RequestParam String repoUrl,
            @RequestParam String jenkinsfilePath
    ) throws IOException {
        // Используем новый сервис для загрузки артефакта
        Path tempZip = artifactDownloaderService.downloadArtifact(jobName, "build.zip");

        // Сохранение информации о деплое
        Deploy deployInfo = new Deploy(port, jobName, repoUrl, jenkinsfilePath);
        deployService.saveDeployInformation(deployInfo);

        deployService.deployFrontend(tempZip, port);
        return ResponseEntity.ok("Frontend развернут на порту: " + port + ". Информация о деплое сохранена.");
    }
}
