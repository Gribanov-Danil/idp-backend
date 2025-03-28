package com.example.idpbackend.dto;

public class GitHubTokenRequest {
    private String username; // Имя пользователя GitHub
    private String personalAccessToken; // Personal Access Token

    // Геттер для username
    public String getUsername() {
        return username;
    }

    // Сеттер для username
    public void setUsername(String username) {
        this.username = username;
    }

    // Геттер для personalAccessToken
    public String getPersonalAccessToken() {
        return personalAccessToken;
    }

    // Сеттер для personalAccessToken
    public void setPersonalAccessToken(String personalAccessToken) {
        this.personalAccessToken = personalAccessToken;
    }
}
