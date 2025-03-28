package com.example.idpbackend.dto;

import java.util.UUID;

public class GetRepoInfoRequest {
    private String repoUrl; // Ссылка на репозиторий
    private UUID userId; // ID пользователя, чей PAT будет использоваться

    // Геттеры и сеттеры
    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
