package com.example.idpbackend.dto;

import java.util.UUID;

public class CreateRepoRequest {
    private UUID userId; // ID пользователя, чей PAT будет использоваться
    private String orgName; // Название организации
    private String repoName; // Название репозитория
    private String description; // Описание репозитория
    private boolean isPrivate;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
