package com.example.idpbackend.controller;

import com.example.idpbackend.dto.CreateGroupRequest;
import com.example.idpbackend.dto.GroupRequest;
import com.example.idpbackend.dto.ImportGroupRequest;
import com.example.idpbackend.entity.Group;
import com.example.idpbackend.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("/create")
    public Group createGroup(@RequestBody CreateGroupRequest createGroupRequest) {
        return groupService.createGroup(createGroupRequest);
    }

    @PostMapping("/import")
    public Group importGroup(@RequestBody ImportGroupRequest importGroupRequest) {
        return groupService.importGroup(importGroupRequest);
    }

    @GetMapping("/{id}")
    public Group getGroup(@PathVariable UUID id) {
        return groupService.getGroupById(id);
    }
}