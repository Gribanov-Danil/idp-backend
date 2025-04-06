package com.example.idpbackend.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.idpbackend.config.JenkinsProperties;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.util.Base64;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class JenkinsService {

    private final JenkinsProperties properties;
    private final RestTemplate restTemplate;
    private final Configuration freemarkerConfig;
    private static final Logger logger = LoggerFactory.getLogger(JenkinsService.class);


    public JenkinsService(JenkinsProperties properties, Configuration freemarkerConfig) {
        this.properties = properties;
        this.freemarkerConfig = freemarkerConfig;
        this.restTemplate = new RestTemplate();
    }

    public void createJob(String jobName, String configXml) {
        logger.info("Generated config.xml: \n{}", configXml);
        String url = properties.getUrl() + "/createItem?name=" + jobName;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.set("Authorization", buildBasicAuth());
        headers.set("Jenkins-Crumb", getCrumb()); // добавляем CSRF-защиту

        HttpEntity<String> request = new HttpEntity<>(configXml, headers);
        logger.info("url: \n{}", url);
        logger.info("Request: \n{}", request);


        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to create Jenkins job: " + response.getBody());
        }
    }

    public String getCrumb() {
        String url = properties.getUrl() + "/crumbIssuer/api/json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildBasicAuth());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().get("crumb").toString();
        } else {
            throw new RuntimeException("Не удалось получить Jenkins crumb: " + response.getStatusCode());
        }
    }

    public void triggerJob(String jobName) {
        String url = properties.getUrl() + "/job/" + jobName + "/build";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildBasicAuth());
        headers.set("Jenkins-Crumb", getCrumb());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to trigger job: " + response.getBody());
        }
    }

    public String getJobStatus(String jobName) {
        String url = properties.getUrl() + "/job/" + jobName + "/lastBuild/api/json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildBasicAuth());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Не удалось получить статус Jenkins job");
        }

        Boolean building = (Boolean) response.getBody().get("building");
        String result = (String) response.getBody().get("result");

        return building != null && building ? "IN_PROGRESS" : result;
    }

    public String generateJobConfigXml(String jobName, String repoUrl, String jenkinsfilePath, Map<String, Object> variables) {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("jobName", jobName);
            model.put("repoUrl", repoUrl);
            model.put("jenkinsfilePath", jenkinsfilePath);
            model.put("variables", variables);

            Template template = freemarkerConfig.getTemplate("template.ftl");
            StringWriter writer = new StringWriter();
            template.process(model, writer);

            return writer.toString();

        } catch (IOException | TemplateException e) {
            throw new RuntimeException("Ошибка генерации Jenkins config.xml", e);
        }
    }

    private String buildBasicAuth() {
        String raw = properties.getUser() + ":" + properties.getToken();
        logger.info("Raw basic auth string before encoding: {}", raw);
        return "Basic " + Base64.getEncoder().encodeToString(raw.getBytes());
    }
}
