package com.example.idpbackend.controller;

import com.example.idpbackend.dto.CreateRepoRequest;
import com.example.idpbackend.dto.GetRepoInfoRequest;
import com.example.idpbackend.dto.GitHubConnectRequest;
import com.example.idpbackend.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    @Autowired
    private GitHubService githubService;

    @PostMapping("/create-repo")
    public ResponseEntity<String> createRepository(@RequestBody CreateRepoRequest request) {
        String result = githubService.createRepository(
                request.getUserId(),
                request.getOrgName(),
                request.getRepoName(),
                request.getDescription(),
                request.isPrivate(),
                request.getJenkinsfilePath()
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping("/get-repo-info")
    public ResponseEntity<String> getRepositoryInfo(@RequestBody GetRepoInfoRequest request) {
        String result = githubService.getRepositoryInfo(request.getRepoUrl(), request.getUserId());
        return ResponseEntity.ok(result);
    }
}