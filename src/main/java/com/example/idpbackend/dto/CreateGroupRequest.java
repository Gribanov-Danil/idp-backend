package com.example.idpbackend.dto;

import com.example.idpbackend.utils.GroupType;

import java.util.UUID;

public class CreateGroupRequest {
    private String name;
    private GroupType type; // "github" или "gitlab"
    private UUID userId; // Personal Access Token
    private String personalAccessToken;

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getPersonalAccessToken() {
        return personalAccessToken;
    }

    public void setPersonalAccessToken(String personalAccessToken) {
        this.personalAccessToken = personalAccessToken;
    }
}
