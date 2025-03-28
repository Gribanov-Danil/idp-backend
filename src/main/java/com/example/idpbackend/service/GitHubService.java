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
        // Находим пользователя по ID
        Optional<GitHubUserIntegration> userIntegrationOpt = userIntegrationRepository.findById(userId);
        if (userIntegrationOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Получаем PAT пользователя
        String personalAccessToken = userIntegrationOpt.get().getPersonalAccessToken();
        UUID integrationId = userIntegrationOpt.get().getId(); // Получаем ID интеграции

        // Формируем URL для запроса информации о репозитории
        String apiUrl = convertRepoUrlToApiUrl(repoUrl);

        // Устанавливаем заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(personalAccessToken); // Используем PAT для авторизации

        // Создаем HTTP-запрос
        HttpEntity<String> request = new HttpEntity<>(headers);

        // Отправляем запрос
        ResponseEntity<GitHubRepoResponse> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, request, GitHubRepoResponse.class
        );

        // Проверяем ответ
        if (response.getStatusCode().is2xxSuccessful()) {
            GitHubRepoResponse repoResponse = response.getBody();
            if (repoResponse != null) {
                // Сохраняем информацию о репозитории в базу данных
                saveRepositoryInfo(repoResponse, integrationId);
                return "Repository info saved successfully!";
            } else {
                throw new RuntimeException("Failed to parse GitHub response");
            }
        } else {
            throw new RuntimeException("Failed to fetch repository info: " + response.getBody());
        }
    }

    private String convertRepoUrlToApiUrl(String repoUrl) {
        // Пример: https://github.com/org/repo -> https://api.github.com/repos/org/repo
        return repoUrl.replace("https://github.com/", "https://api.github.com/repos/");
    }

    public String createRepository(UUID userId, String orgName, String repoName, String description, boolean isPrivate) {
        // Находим пользователя по ID
        Optional<GitHubUserIntegration> userIntegrationOpt = repository.findById(userId);
        if (userIntegrationOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Получаем PAT пользователя
        String personalAccessToken = userIntegrationOpt.get().getPersonalAccessToken();
        UUID integrationId = userIntegrationOpt.get().getId(); // Получаем ID интеграции

        // Формируем URL для создания репозитория
        String url = "https://api.github.com/orgs/" + orgName + "/repos";

        // Формируем тело запроса
        String requestBody = String.format(
                "{\"name\": \"%s\", \"description\": \"%s\", \"private\": %b}",
                repoName, description, isPrivate
        );

        // Устанавливаем заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(personalAccessToken);

        // Создаем HTTP-запрос
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // Отправляем запрос
        ResponseEntity<GitHubRepoResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, request, GitHubRepoResponse.class);

        // Возвращаем результат
        if (response.getStatusCode().is2xxSuccessful()) {
            GitHubRepoResponse repoResponse = response.getBody();
            if (repoResponse != null) {
                // Сохраняем информацию о репозитории в базу данных
                saveRepositoryInfo(repoResponse, integrationId);
                return "Repository created successfully!";
            } else {
                throw new RuntimeException("Failed to parse GitHub response");
            }
        } else {
            throw new RuntimeException("Failed to create repository: " + response.getBody());
        }
    }

    private void saveRepositoryInfo(GitHubRepoResponse repoResponse, UUID integrationId) {
        // Находим объект GitHubUserIntegration по его ID
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

        repositoryRepository.save(repository);
    }

    private LocalDateTime parseGitHubDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return LocalDateTime.parse(date, formatter);
    }

    /**
     * Создает новую организацию в GitHub
     * @param userId UUID пользователя в системе
     * @param orgName Название организации для создания
     * @return Сообщение об успешном создании
     * @throws RuntimeException если пользователь не найден или GitHub API вернул ошибку
     */
    public String createGitHubOrganization(UUID userId, String orgName) {
        // 1. Находим пользователя по ID
        Optional<GitHubUserIntegration> userIntegrationOpt = repository.findById(userId);
        if (userIntegrationOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // 2. Получаем PAT пользователя
        String personalAccessToken = userIntegrationOpt.get().getPersonalAccessToken();
        UUID integrationId = userIntegrationOpt.get().getId();

        // 3. Формируем URL и тело запроса
        String url = "https://api.github.com/orgs";
        String requestBody = String.format("{\"login\": \"%s\"}", orgName);

        // 4. Устанавливаем заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(personalAccessToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        // 5. Создаем и отправляем запрос
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<GitHubOrgResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                GitHubOrgResponse.class
        );

        // 6. Обрабатываем ответ
        if (response.getStatusCode().is2xxSuccessful()) {
            GitHubOrgResponse orgResponse = response.getBody();
            if (orgResponse != null) {
                // Здесь можно сохранить информацию о созданной организации
                return "Organization '" + orgResponse.getLogin() + "' created successfully!";
            }
            throw new RuntimeException("Failed to parse GitHub response");
        }
        throw new RuntimeException("Failed to create organization: " + response.getBody());
    }
}