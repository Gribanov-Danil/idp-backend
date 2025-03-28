package com.example.idpbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitHubRepoResponse {

    private Long id; // ID репозитория
    private String name; // Название репозитория

    @JsonProperty("full_name")
    private String fullName; // Полное название репозитория

    private String description; // Описание репозитория

    @JsonProperty("private")
    private boolean isPrivate; // Приватный или публичный репозиторий

    @JsonProperty("html_url")
    private String htmlUrl; // Ссылка на репозиторий

    @JsonProperty("created_at")
    private String createdAt; // Дата создания репозитория

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
