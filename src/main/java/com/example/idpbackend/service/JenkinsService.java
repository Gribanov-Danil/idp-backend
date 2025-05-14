package com.example.idpbackend.service;
import com.example.idpbackend.dto.jenkins.CreatePipelineRequest;
import com.example.idpbackend.service.jenkins.JenkinsApiClient;
import com.example.idpbackend.service.jenkins.JenkinsJobBuildInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.idpbackend.config.JenkinsProperties;
import com.example.idpbackend.entity.Pipeline;
import com.example.idpbackend.repository.PipelineRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class JenkinsService {

    private final JenkinsProperties properties;
    private final Configuration freemarkerConfig;
    private final PipelineRepository pipelineRepository;
    private final ObjectMapper objectMapper;
    private final JenkinsApiClient jenkinsApiClient;
    private static final Logger logger = LoggerFactory.getLogger(JenkinsService.class);


    public JenkinsService(JenkinsProperties properties, Configuration freemarkerConfig, PipelineRepository pipelineRepository, ObjectMapper objectMapper, JenkinsApiClient jenkinsApiClient) {
        this.properties = properties;
        this.freemarkerConfig = freemarkerConfig;
        this.pipelineRepository = pipelineRepository;
        this.objectMapper = objectMapper;
        this.jenkinsApiClient = jenkinsApiClient;
    }

    @Transactional
    public void createOrUpdateJenkinsJobAndPipeline(CreatePipelineRequest requestDto) {
        String jobName = requestDto.getJobName();
        String configXml = generateJobConfigXml(requestDto.getJobName(), requestDto.getRepoUrl(), requestDto.getJenkinsfilePath(), requestDto.getVariables());

        jenkinsApiClient.createOrUpdateJobInJenkins(jobName, configXml);

        saveOrUpdatePipelineEntity(requestDto);
    }

    private void saveOrUpdatePipelineEntity(CreatePipelineRequest requestDto) {
        String jobName = requestDto.getJobName();
        Optional<Pipeline> existingPipelineOptional = pipelineRepository.findByJobName(jobName);
        Pipeline pipeline;

        if (existingPipelineOptional.isPresent()) {
            pipeline = existingPipelineOptional.get();
            logger.info("Обновление существующей записи пайплайна в БД: {}", jobName);
        } else {
            pipeline = new Pipeline();
            pipeline.setJobName(jobName);
            logger.info("Создание новой записи пайплайна в БД: {}", jobName);
        }

        pipeline.setRepoUrl(requestDto.getRepoUrl());
        pipeline.setJenkinsfilePath(requestDto.getJenkinsfilePath());
        try {
            pipeline.setVariables(objectMapper.writeValueAsString(requestDto.getVariables()));
        } catch (JsonProcessingException e) {
            logger.error("Ошибка сериализации переменных пайплайна в JSON для {}: {}", jobName, e.getMessage());
            pipeline.setVariables(null);
        }
        
        pipeline.setLastBuildStatus("NOT_CONFIGURED");
        pipeline.setLastBuildNumber(null);
        pipeline.setLastTriggeredAt(null);

        pipelineRepository.save(pipeline);
        logger.info("Запись пайплайна {} успешно сохранена/обновлена в БД.", jobName);
    }

    @Transactional
    public void triggerJob(String jobName) {
        jenkinsApiClient.triggerJobInJenkins(jobName);

        updatePipelineOnTrigger(jobName);
    }

    private void updatePipelineOnTrigger(String jobName) {
        pipelineRepository.findByJobName(jobName).ifPresent(pipeline -> {
            pipeline.setLastTriggeredAt(LocalDateTime.now());
            pipelineRepository.save(pipeline);
            logger.info("Время последнего запуска для пайплайна {} обновлено в БД.", jobName);
        });
    }

    @Transactional
    public String getJobStatus(String jobName) {
        JenkinsJobBuildInfo buildInfo = jenkinsApiClient.getJobStatusFromJenkins(jobName);

        updatePipelineStatusInDb(jobName, buildInfo);
        
        return buildInfo.getStatus();
    }

    private void updatePipelineStatusInDb(String jobName, JenkinsJobBuildInfo buildInfo) {
        Optional<Pipeline> pipelineOptional = pipelineRepository.findByJobName(jobName);
        if (pipelineOptional.isPresent()) {
            Pipeline pipeline = pipelineOptional.get();
            pipeline.setLastBuildStatus(buildInfo.getStatus());
            pipeline.setLastBuildNumber(buildInfo.getBuildNumber());
            pipelineRepository.save(pipeline);
            logger.info("Статус пайплайна {} ({}) обновлен в БД на {} (сборка #{})", 
                        jobName, pipeline.getId(), buildInfo.getStatus(), buildInfo.getBuildNumber());
        } else {
            logger.warn("Пайплайн с именем {} не найден в БД для обновления статуса.", jobName);
        }
    }

    public String generateJobConfigXml(String jobName, String repoUrl, String jenkinsfilePath, Map<String, Object> variables) {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("jobName", jobName);
            model.put("repoUrl", repoUrl);
            model.put("jenkinsfilePath", jenkinsfilePath != null ? jenkinsfilePath : "Jenkinsfile");
            model.put("variables", variables != null ? variables : new HashMap<>());

            Template template = freemarkerConfig.getTemplate("template.ftl");
            StringWriter writer = new StringWriter();
            template.process(model, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            logger.error("Ошибка генерации Jenkins config.xml для {}: {}", jobName, e.getMessage());
            throw new RuntimeException("Ошибка генерации Jenkins config.xml", e);
        }
    }
}
