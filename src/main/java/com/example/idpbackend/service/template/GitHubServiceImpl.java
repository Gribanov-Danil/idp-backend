package com.example.idpbackend.service.template;

import org.kohsuke.github.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Service
@Primary
public class GitHubServiceImpl implements GitHubService {

    private final GitHub github;
    private final RestTemplate restTemplate;
    private final String token;
    private static final Logger logger = LoggerFactory.getLogger(GitHubServiceImpl.class);

    public GitHubServiceImpl(RestTemplate restTemplate, @Value("${spring.github.token}") String token) throws IOException {
        this.restTemplate = restTemplate;
        this.token = token;
        try {
            this.github = new GitHubBuilder().withOAuthToken(token).build();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании GitHub клиента", e);
        }
    }

    @Override
    public GHRepository createRepositoryFromTemplate(String organizationName, String newRepoName, String templateRepoFullName) throws IOException {
        // 1. Получаем организацию
        GHOrganization organization = github.getOrganization(organizationName);

        // 2. Создаём пустой репозиторий
        GHRepository newRepo = organization.createRepository(newRepoName)
                .description("Создан из шаблона " + templateRepoFullName)
                .private_(true) // если нужно
                .autoInit(false) // без README
                .create();

        // 3. Получаем шаблонный репозиторий
        GHRepository templateRepo = github.getRepository(templateRepoFullName);

        // 4. Возвращаем оба — обработка содержимого будет в другом методе
        return newRepo;
    }


    @Override
    public GHTree getRepositoryTree(String repositoryFullName) throws IOException {
        GHRepository repo = github.getRepository(repositoryFullName);
        return repo.getTreeRecursive("master", 1); // Используем ветку main
    }


    @Override
    public byte[] getFileContent(String repositoryFullName, String filePath) throws IOException {
        GHRepository repo = github.getRepository(repositoryFullName);
        return repo.getFileContent(filePath).read().readAllBytes();
    }

    @Override
    public void createOrUpdateFile(String repositoryFullName, String path, String content, String commitMessage) throws IOException {
        GHRepository repo = github.getRepository(repositoryFullName);
        String branch = repo.getDefaultBranch();

        try {
            // если файл уже есть — обновим
            GHContent file = repo.getFileContent(path);
            file.update(content, commitMessage, branch);
        } catch (GHFileNotFoundException e) {
            // если файла нет — создадим
            repo.createContent()
                    .path(path)
                    .content(content)
                    .message(commitMessage)
                    .branch(branch)
                    .commit();
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private GHRepository waitForFork(String fullName) throws IOException {
        int retries = 10;
        int delayMs = 2000;

        for (int i = 0; i < retries; i++) {
            try {
                return github.getRepository(fullName);
            } catch (GHFileNotFoundException e) {
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ignored) {
                }
            }
        }

        throw new IOException("Timed out waiting for fork: " + fullName);
    }

    private String extractRepositoryNameOnly(String repositoryFullName) {
        // "user/repo" -> "repo"
        return repositoryFullName.substring(repositoryFullName.indexOf('/') + 1);
    }

    private void waitUntilRepositoryIsReady(GHRepository repo) throws IOException {
        int retries = 10;
        int delayMillis = 2000;

        for (int i = 0; i < retries; i++) {
            GHRepository refreshedRepo = github.getRepository(repo.getFullName());
            if (refreshedRepo.getSize() > 0) {
                logger.info("Repository is ready: {}", refreshedRepo.getFullName());
                return;
            }
            logger.info("Waiting for repository to be ready... attempt {}", i + 1);
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while waiting for repo to be ready", e);
            }
        }

        throw new IOException("Timeout: Repository is not ready after retries");
    }
}