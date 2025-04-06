package com.example.idpbackend.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class DeployService {

    public void deployFrontend(Path zipPath, int port) throws IOException {
        Path deployDir = Paths.get("C:/Users/Danil/frontend-apps/app-" + port);
        if (!Files.exists(deployDir)) {
            Files.createDirectories(deployDir);
        }

        unzip(zipPath, deployDir);

        Path buildDir = deployDir.resolve("build"); // путь до build/

        new ProcessBuilder("serve.cmd", "-s", buildDir.toString(), "-l", String.valueOf(port))
                .redirectOutput(new File("serve-" + port + ".log"))
                .start();
    }

    private void unzip(Path zipFilePath, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path filePath = targetDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zis, filePath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}


//docker run -d --name jenkins --network jenkins-net -p 9090:8080 -p 50000:50000 -v jenkins_home:/var/jenkins_home --dns=8.8.8.8 jenkins/jenkins:lts
//docker run -d --name jenkins --network jenkins-net2 --dns=8.8.8.8 -p 9090:8080 -p 50000:50000 -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts
//
//docker run -d --name jenkins --network jenkins-net2 -p 9090:8080 -p 50000:50000 -v jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock jenkins/jenkins:lts
//docker run -d --name jenkins --network jenkins-net2 --dns=8.8.8.8 -p 9090:8080 -p 50000:50000 -v jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock --user root jenkins-docker