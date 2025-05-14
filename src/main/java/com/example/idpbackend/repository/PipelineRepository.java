package com.example.idpbackend.repository;

import com.example.idpbackend.entity.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, Long> {
    Optional<Pipeline> findByJobName(String jobName);
} 