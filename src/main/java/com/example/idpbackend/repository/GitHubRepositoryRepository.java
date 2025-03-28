package com.example.idpbackend.repository;


import com.example.idpbackend.entity.GitHubRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GitHubRepositoryRepository extends JpaRepository<GitHubRepository, UUID> {
}
