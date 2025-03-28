package com.example.idpbackend.repository;

import com.example.idpbackend.entity.GitHubIntegration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GitHubIntegrationRepository extends JpaRepository<GitHubIntegration, UUID> {
    Optional<GitHubIntegration> findByOrganizationName(String organizationName);
}