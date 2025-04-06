package com.example.idpbackend.dto;

public class GitHubTokenRequest {
    private String username; // Имя пользователя GitHub
    private String personalAccessToken; // Personal Access Token

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonalAccessToken() {
        return personalAccessToken;
    }

    public void setPersonalAccessToken(String personalAccessToken) {
        this.personalAccessToken = personalAccessToken;
    }
}
