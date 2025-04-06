package com.example.idpbackend.service;

import com.example.idpbackend.dto.GitHubOrgResponse;
import com.example.idpbackend.dto.GitHubRepoResponse;
import com.example.idpbackend.entity.GitHubRepository;
import com.example.idpbackend.repository.GitHubRepositoryRepository;
import org.springframework.stereotype.Service;
import com.example.idpbackend.entity.GitHubUserIntegration;
import com.example.idpbackend.repository.GitHubUserIntegrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class GitHubService {

    @Autowired
    private GitHubUserIntegrationRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GitHubUserIntegrationRepository userIntegrationRepository;

    @Autowired
    private GitHubRepositoryRepository repositoryRepository;

    public String getRepositoryInfo(String repoUrl, UUID userId) {
        Optional<GitHubUserIntegration> userIntegrationOpt = userIntegrationRepository.findById(userId);
        if (userIntegrationOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        String personalAccessToken = userIntegrationOpt.get().getPersonalAccessToken();
        UUID integrationId = userIntegrationOpt.get().getId();

        String apiUrl = convertRepoUrlToApiUrl(repoUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(personalAccessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<GitHubRepoResponse> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, request, GitHubRepoResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            GitHubRepoResponse repoResponse = response.getBody();
            if (repoResponse != null) {
                saveRepositoryInfo(repoResponse, integrationId, null); // без Jenkinsfile
                return "Repository info saved successfully!";
            } else {
                throw new RuntimeException("Failed to parse GitHub response");
            }
        } else {
            throw new RuntimeException("Failed to fetch repository info: " + response.getBody());
        }
    }

    private String convertRepoUrlToApiUrl(String repoUrl) {
        return repoUrl.replace("https://github.com/", "https://api.github.com/repos/");
    }

    public String createRepository(UUID userId, String orgName, String repoName, String description, boolean isPrivate, String jenkinsfilePath) {
        Optional<GitHubUserIntegration> userIntegrationOpt = repository.findById(userId);
        if (userIntegrationOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        String personalAccessToken = userIntegrationOpt.get().getPersonalAccessToken();
        UUID integrationId = userIntegrationOpt.get().getId();

        String url = "https://api.github.com/orgs/" + orgName + "/repos";

        String requestBody = String.format(
                "{\"name\": \"%s\", \"description\": \"%s\", \"private\": %b}",
                repoName, description, isPrivate
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(personalAccessToken);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<GitHubRepoResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, request, GitHubRepoResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            GitHubRepoResponse repoResponse = response.getBody();
            if (repoResponse != null) {
                saveRepositoryInfo(repoResponse, integrationId, jenkinsfilePath);
                return "Repository created successfully!";
            } else {
                throw new RuntimeException("Failed to parse GitHub response");
            }
        } else {
            throw new RuntimeException("Failed to create repository: " + response.getBody());
        }
    }

    private void saveRepositoryInfo(GitHubRepoResponse repoResponse, UUID integrationId, String jenkinsfilePath) {
        GitHubUserIntegration integration = userIntegrationRepository.findById(integrationId)
                .orElseThrow(() -> new RuntimeException("Integration not found"));

        GitHubRepository repository = new GitHubRepository();
        repository.setRepoId(repoResponse.getId());
        repository.setName(repoResponse.getName());
        repository.setFullName(repoResponse.getFullName());
        repository.setDescription(repoResponse.getDescription());
        repository.setPrivate(repoResponse.isPrivate());
        repository.setHtmlUrl(repoResponse.getHtmlUrl());
        repository.setCreatedAt(parseGitHubDate(repoResponse.getCreatedAt()));
        repository.setIntegration(integration);

        if (jenkinsfilePath != null) {
            repository.setJenkinsfilePath(jenkinsfilePath);
        }

        repositoryRepository.save(repository);
    }

    private LocalDateTime parseGitHubDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return LocalDateTime.parse(date, formatter);
    }

    public String createGitHubOrganization(UUID userId, String orgName) {
        Optional<GitHubUserIntegration> userIntegrationOpt = repository.findById(userId);
        if (userIntegrationOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        String personalAccessToken = userIntegrationOpt.get().getPersonalAccessToken();
        UUID integrationId = userIntegrationOpt.get().getId();

        String url = "https://api.github.com/orgs";
        String requestBody = String.format("{\"login\": \"%s\"}", orgName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(personalAccessToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<GitHubOrgResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, request, GitHubOrgResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            GitHubOrgResponse orgResponse = response.getBody();
            if (orgResponse != null) {
                return "Organization '" + orgResponse.getLogin() + "' created successfully!";
            }
            throw new RuntimeException("Failed to parse GitHub response");
        }
        throw new RuntimeException("Failed to create organization: " + response.getBody());
    }
}
