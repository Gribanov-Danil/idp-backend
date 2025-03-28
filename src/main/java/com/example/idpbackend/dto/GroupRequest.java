package com.example.idpbackend.dto;

import com.example.idpbackend.utils.GroupType;

public class GroupRequest {

    private String name; // Название группы
    private GroupType type; // Тип группы (GitHub или GitLab)
    private String externalId; // Внешний идентификатор группы (для импорта)
    private String personalAccessToken; // PAT для управления группой

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

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getPersonalAccessToken() {
        return personalAccessToken;
    }

    public void setPersonalAccessToken(String personalAccessToken) {
        this.personalAccessToken = personalAccessToken;
    }
}
