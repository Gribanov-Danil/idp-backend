package com.example.idpbackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class JenkinsArtifactDownloaderService implements ArtifactDownloaderService {

    // Базовый URL Jenkins можно вынести в конфигурацию
    @Value("${jenkins.baseurl:http://localhost:9090}") // Значение по умолчанию, если не задано в properties
    private String jenkinsBaseUrl;

    @Override
    public Path downloadArtifact(String jobName, String artifactFileName) throws IOException {
        // Формируем URL для артефакта
        String artifactUrlString = String.format("%s/job/%s/lastSuccessfulBuild/artifact/%s",
                jenkinsBaseUrl, jobName, artifactFileName);
        
        Path tempZip = Paths.get("temp-" + jobName + "-" + artifactFileName);

        try (InputStream in = new URL(artifactUrlString).openStream()) {
            Files.copy(in, tempZip, StandardCopyOption.REPLACE_EXISTING);
        }
        return tempZip;
    }
} 