package com.example.idpbackend.service;


import com.example.idpbackend.dto.GitHubTokenRequest;
import com.example.idpbackend.entity.GitHubUserIntegration;
import com.example.idpbackend.repository.GitHubUserIntegrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GitHubTokenService {
    @Autowired
    private GitHubUserIntegrationRepository repository;

    public GitHubUserIntegration saveToken(GitHubTokenRequest request) {
        // Проверяем, существует ли уже запись для этого пользователя
        GitHubUserIntegration integration = repository.findByUsername(request.getUsername())
                .orElse(new GitHubUserIntegration());

        // Обновляем данные
        integration.setUsername(request.getUsername());
        integration.setPersonalAccessToken(request.getPersonalAccessToken());

        // Сохраняем в базу данных
        return repository.save(integration);
    }
}
