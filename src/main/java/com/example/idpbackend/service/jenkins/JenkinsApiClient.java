package com.example.idpbackend.service.jenkins;

import com.example.idpbackend.config.JenkinsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Component
public class JenkinsApiClient {

    private static final Logger logger = LoggerFactory.getLogger(JenkinsApiClient.class);
    private final JenkinsProperties properties;
    private final RestTemplate restTemplate;

    public JenkinsApiClient(JenkinsProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    private String buildBasicAuth() {
        String raw = properties.getUser() + ":" + properties.getToken();
        return "Basic " + Base64.getEncoder().encodeToString(raw.getBytes());
    }

    public String getCrumb() {
        String url = properties.getUrl() + "/crumbIssuer/api/json";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildBasicAuth());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().containsKey("crumb")) {
                return (String) response.getBody().get("crumb");
            }
            logger.error("Не удалось получить Jenkins crumb. Код ответа: {}. Тело: {}", response.getStatusCode(), response.getBody());
            throw new RuntimeException("Не удалось получить Jenkins crumb: " + response.getStatusCode());
        } catch (Exception e) {
            logger.error("Ошибка при получении Jenkins crumb: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при получении Jenkins crumb", e);
        }
    }

    public boolean jobExists(String jobName) {
        String url = properties.getUrl() + "/job/" + jobName + "/api/json";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildBasicAuth());
        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound e) {
            logger.debug("Задача {} не найдена в Jenkins.", jobName);
            return false;
        } catch (Exception e) {
            logger.error("Ошибка при проверке существования задачи {} в Jenkins: {}", jobName, e.getMessage(), e);
            // В зависимости от политики, можно либо вернуть false, либо пробросить исключение
            return false; // Или throw new RuntimeException("Ошибка проверки задачи", e);
        }
    }

    public void createOrUpdateJobInJenkins(String jobName, String configXml) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.set("Authorization", buildBasicAuth());
        headers.set("Jenkins-Crumb", getCrumb());
        HttpEntity<String> requestEntity = new HttpEntity<>(configXml, headers);

        String url;
        HttpMethod method;

        if (jobExists(jobName)) {
            logger.info("Задача {} уже существует в Jenkins. Обновление конфигурации.", jobName);
            url = properties.getUrl() + "/job/" + jobName + "/config.xml";
            method = HttpMethod.POST; // Jenkins API для обновления конфигурации джобы использует POST на /job/{name}/config.xml
        } else {
            logger.info("Задача {} не существует в Jenkins. Создание новой задачи.", jobName);
            url = properties.getUrl() + "/createItem?name=" + jobName;
            method = HttpMethod.POST;
        }

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, method, requestEntity, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                String action = method == HttpMethod.POST && url.contains("/config.xml") ? "обновить" : "создать";
                logger.error("Не удалось {} задачу Jenkins {}: {} - {}", action, jobName, response.getStatusCode(), response.getBody());
                throw new RuntimeException("Не удалось " + action + " задачу Jenkins: " + response.getBody());
            }
            String actionPast = method == HttpMethod.POST && url.contains("/config.xml") ? "обновлена" : "создана";
            logger.info("Задача Jenkins {} успешно {}.", jobName, actionPast);
        } catch (Exception e) {
            String action = method == HttpMethod.POST && url.contains("/config.xml") ? "обновлении" : "создании";
            logger.error("Ошибка при {} задачи Jenkins {}: {}", action, jobName, e.getMessage(), e);
            throw new RuntimeException("Ошибка при " + action + " задачи Jenkins: " + e.getMessage(), e);
        }
    }

    public void triggerJobInJenkins(String jobName) {
        String url = properties.getUrl() + "/job/" + jobName + "/build";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildBasicAuth());
        headers.set("Jenkins-Crumb", getCrumb());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Не удалось запустить задачу {}: {} - {}", jobName, response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to trigger job: " + response.getBody());
            }
            logger.info("Задача {} успешно запущена.", jobName);
        } catch (Exception e) {
            logger.error("Ошибка при запуске задачи {}: {}", jobName, e.getMessage(), e);
            throw new RuntimeException("Ошибка при запуске задачи " + jobName + ": " + e.getMessage(), e);
        }
    }

    public JenkinsJobBuildInfo getJobStatusFromJenkins(String jobName) {
        String url = properties.getUrl() + "/job/" + jobName + "/lastBuild/api/json";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildBasicAuth());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Boolean building = (Boolean) body.get("building");
                String result = (String) body.get("result");
                Integer buildNumber = (Integer) body.get("number");

                if (building != null && building) {
                    return JenkinsJobBuildInfo.building(buildNumber);
                }
                return JenkinsJobBuildInfo.completed(result, buildNumber);
            }
            logger.warn("Не удалось получить статус для Jenkins задачи {}, код ответа: {}. Возможно, задача еще не собиралась.", jobName, response.getStatusCode());
            return JenkinsJobBuildInfo.notBuilt();
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Последняя сборка для задачи {} не найдена. Возможно, задача еще не запускалась.", jobName);
            return JenkinsJobBuildInfo.notBuilt();
        } catch (Exception e) {
            logger.error("Ошибка при получении статуса Jenkins задачи {}: {}.", jobName, e.getMessage(), e);
            return JenkinsJobBuildInfo.errorFetching();
        }
    }
} 