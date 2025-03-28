package com.example.idpbackend.controller;

import com.example.idpbackend.dto.GitHubTokenRequest;
import com.example.idpbackend.entity.GitHubUserIntegration;
import com.example.idpbackend.service.GitHubTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github/integration")
public class GitHubTokenController {

    @Autowired
    private GitHubTokenService tokenService;

    @PostMapping("/save-user-token")
    public ResponseEntity<GitHubUserIntegration> saveToken(@RequestBody GitHubTokenRequest request) {
        GitHubUserIntegration integration = tokenService.saveToken(request);
        return ResponseEntity.ok(integration);
    }
}
