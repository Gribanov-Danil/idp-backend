package com.example.idpbackend.service;

import com.example.idpbackend.dto.CreateGroupRequest;
import com.example.idpbackend.dto.ImportGroupRequest;
import com.example.idpbackend.entity.Group;
import com.example.idpbackend.repository.GroupRepository;
import com.example.idpbackend.utils.GroupType;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GitHubService gitHubService;
//    private final GitLabService gitLabService;

    public GroupService(GroupRepository groupRepository, GitHubService gitHubService) {
        this.groupRepository = groupRepository;
        this.gitHubService = gitHubService;
//        this.gitLabService = gitLabService;
    }

    public Group createGroup(CreateGroupRequest createGroupRequest) {
        Group group = new Group();
        group.setName(createGroupRequest.getName());
        group.setType(createGroupRequest.getType());
        group.setPersonalAccessToken(createGroupRequest.getPersonalAccessToken());
        group.setImported(false);

        if (GroupType.GITHUB.toString().equalsIgnoreCase(createGroupRequest.getType().toString())) {
            String externalId = gitHubService.createGitHubOrganization(createGroupRequest.getUserId(), createGroupRequest.getName());
            group.setExternalId(externalId);
        } else if (GroupType.GITHUB.toString().equalsIgnoreCase(createGroupRequest.getType().toString())) {
//            String externalId = gitLabService.createGitLabGroup(createGroupRequest.getPat(), createGroupRequest.getName());
//            group.setExternalId(externalId);
        }

        return groupRepository.save(group);
    }

    public Group importGroup(ImportGroupRequest importGroupRequest) {
        Group group = new Group();
        group.setName(importGroupRequest.getName());
        group.setType(importGroupRequest.getType());
        group.setPersonalAccessToken(importGroupRequest.getPat());
        group.setExternalId(importGroupRequest.getExternalId());
        group.setImported(true);

        return groupRepository.save(group);
    }

    public Group getGroupById(UUID id) {
        return groupRepository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));
    }
}