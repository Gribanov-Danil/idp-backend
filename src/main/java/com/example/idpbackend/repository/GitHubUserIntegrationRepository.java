package com.example.idpbackend.repository;

import com.example.idpbackend.entity.GitHubUserIntegration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GitHubUserIntegrationRepository extends JpaRepository<GitHubUserIntegration, UUID> {
    Optional<GitHubUserIntegration> findByUsername(String username);
}
