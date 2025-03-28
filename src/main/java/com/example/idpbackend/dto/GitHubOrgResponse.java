package com.example.idpbackend.dto;

public class GitHubOrgResponse {
    private String login;
    private Long id;
    private String url;
    private String reposUrl;
    private String description;

    public String getLogin() {
        return login;
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getReposUrl() {
        return reposUrl;
    }

    public String getDescription() {
        return description;
    }

    // Сеттеры
    public void setLogin(String login) {
        this.login = login;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setReposUrl(String reposUrl) {
        this.reposUrl = reposUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
