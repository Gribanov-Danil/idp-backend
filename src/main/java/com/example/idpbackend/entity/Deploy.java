package com.example.idpbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Сущность для хранения информации о деплое.
 */
@Entity
@Table(name = "deploys")
public class Deploy extends BaseEntity {

    @NotNull(message = "Порт не может быть пустым")
    @Column(nullable = false)
    private Integer port;

    @NotBlank(message = "Имя задачи Jenkins не может быть пустым")
    @Column(nullable = false)
    private String jobName;

    @NotBlank(message = "URL репозитория не может быть пустым")
    @Column(nullable = false)
    private String repoUrl;

    @NotBlank(message = "Путь к Jenkinsfile не может быть пустым")
    @Column(nullable = false)
    private String jenkinsfilePath;

    // Constructors
    public Deploy() {
    }

    public Deploy(Integer port, String jobName, String repoUrl, String jenkinsfilePath) {
        this.port = port;
        this.jobName = jobName;
        this.repoUrl = repoUrl;
        this.jenkinsfilePath = jenkinsfilePath;
    }

    // Getters and Setters
    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getJenkinsfilePath() {
        return jenkinsfilePath;
    }

    public void setJenkinsfilePath(String jenkinsfilePath) {
        this.jenkinsfilePath = jenkinsfilePath;
    }

    // toString, equals, hashCode (опционально, но полезно)
    @Override
    public String toString() {
        return "Deploy{" +
                "id=" + getId() +
                ", port=" + port +
                ", jobName='" + jobName + "'" +
                ", repoUrl='" + repoUrl + "'" +
                ", jenkinsfilePath='" + jenkinsfilePath + "'" +
                '}';
    }
} 