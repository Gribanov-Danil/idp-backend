package com.example.idpbackend.dto;

import com.example.idpbackend.utils.GroupType;

public class ImportGroupRequest {
    private String name;
    private GroupType type; // "github" или "gitlab"
    private String pat; // Personal Access Token
    private String externalId; // ID группы в GitHub/GitLab

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

    public String getPat() {
        return pat;
    }

    public void setPat(String pat) {
        this.pat = pat;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
