package com.example.idpbackend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "github_user_integration")
public class GitHubUserIntegration {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username; // Имя пользователя GitHub

    @Column(name = "personal_access_token", nullable = false)
    private String personalAccessToken; // Personal Access Token

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "integration", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GitHubRepository> repositories = new ArrayList<>();

    // Геттер для id
    public UUID getId() {
        return id;
    }

    // Сеттер для id (обычно не используется, так как id генерируется автоматически)
    public void setId(UUID id) {
        this.id = id;
    }

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

    // Геттер для createdAt
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Сеттер для createdAt (обычно не используется, так как значение устанавливается автоматически)
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Геттер для updatedAt
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Сеттер для updatedAt (обычно не используется, так как значение обновляется автоматически)
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<GitHubRepository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<GitHubRepository> repositories) {
        this.repositories = repositories;
    }
}
