package com.example.idpbackend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "github_repository")
public class GitHubRepository {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "repo_id", nullable = false, unique = true)
    private Long repoId; // ID репозитория в GitHub

    @Column(name = "name", nullable = false)
    private String name; // Название репозитория

    @Column(name = "full_name", nullable = false)
    private String fullName; // Полное название репозитория (например, org/repo)

    @Column(name = "description")
    private String description; // Описание репозитория

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate; // Приватный или публичный репозиторий

    @Column(name = "html_url", nullable = false)
    private String htmlUrl; // Ссылка на репозиторий

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // Дата создания репозитория

    @CreationTimestamp
    @Column(name = "saved_at", updatable = false)
    private LocalDateTime savedAt; // Дата сохранения в базу данных

    @ManyToOne
    @JoinColumn(name = "integration_id", nullable = false)
    private GitHubUserIntegration integration; // ID интеграции из таблицы github_user_integration

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getRepoId() {
        return repoId;
    }

    public void setRepoId(Long repoId) {
        this.repoId = repoId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    public GitHubUserIntegration getIntegrationId() {
        return integration;
    }

    public void setIntegration(GitHubUserIntegration integration) {
        this.integration = integration;
    }
}