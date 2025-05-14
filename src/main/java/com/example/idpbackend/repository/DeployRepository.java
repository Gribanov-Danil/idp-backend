package com.example.idpbackend.repository;

import com.example.idpbackend.entity.Deploy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностями Deploy.
 */
@Repository
public interface DeployRepository extends JpaRepository<Deploy, Long> {
    // Здесь можно добавлять кастомные методы для запросов, если потребуется
} 