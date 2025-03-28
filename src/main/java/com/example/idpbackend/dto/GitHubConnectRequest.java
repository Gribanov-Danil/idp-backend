package com.example.idpbackend.dto;

public class GitHubConnectRequest {
    private String organizationUrl;
    private String clientId;
    private String clientSecret;
    private String redirectUri;

    // Геттеры
    public String getOrganizationUrl() {
        return organizationUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    // Сеттеры
    public void setOrganizationUrl(String organizationUrl) {
        this.organizationUrl = organizationUrl;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}