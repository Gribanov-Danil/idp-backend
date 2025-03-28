package com.example.idpbackend.repository;

import com.example.idpbackend.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {
    Optional<Group> findByExternalId(String externalId); // Поиск группы по внешнему идентификатору
}