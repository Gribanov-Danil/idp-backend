package com.example.idpbackend.controller;

import com.example.idpbackend.dto.jenkins.CreatePipelineRequest;
import com.example.idpbackend.service.JenkinsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JenkinsController.class)
@WithMockUser
class JenkinsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JenkinsService jenkinsService;

    @Autowired
    private ObjectMapper objectMapper; // Для сериализации CreatePipelineRequest в JSON

    // --- Тесты для createPipeline ---

    @Test
    void createPipeline_shouldReturnOk_whenRequestIsValid() throws Exception {
        CreatePipelineRequest request = new CreatePipelineRequest("testJob", "http://repo.url", Collections.emptyMap(), "JenkinsfilePath");
        String requestJson = objectMapper.writeValueAsString(request);

        // Настройка мока: doNothing, так как метод сервиса void
        doNothing().when(jenkinsService).createOrUpdateJenkinsJobAndPipeline(request);

        mockMvc.perform(post("/api/jenkins/create-pipeline")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Jenkins job created/updated and pipeline info saved successfully!"));

        // Проверка, что метод сервиса был вызван с правильным аргументом
        verify(jenkinsService).createOrUpdateJenkinsJobAndPipeline(request);
    }

    @Test
    void createPipeline_shouldReturnInternalServerError_whenServiceThrowsException() throws Exception {
        CreatePipelineRequest request = new CreatePipelineRequest("testJob", "http://repo.url", Collections.emptyMap(), "JenkinsfilePath");
        String requestJson = objectMapper.writeValueAsString(request);

        // Настройка мока для выбрасывания исключения
        doThrow(new RuntimeException("Service error")).when(jenkinsService).createOrUpdateJenkinsJobAndPipeline(request);

        mockMvc.perform(post("/api/jenkins/create-pipeline")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isInternalServerError()); 
                // TODO: Уточнить ожидаемый статус, если есть @ControllerAdvice для обработки RuntimeException.
                // Если нет, isInternalServerError() - это стандартное поведение Spring Boot.

        verify(jenkinsService).createOrUpdateJenkinsJobAndPipeline(request);
    }

    // --- Тесты для triggerPipeline ---

    @Test
    void triggerPipeline_shouldReturnOk_whenJobNameIsValid() throws Exception {
        String jobName = "testJob";
        doNothing().when(jenkinsService).triggerJob(jobName);

        mockMvc.perform(post("/api/jenkins/trigger-pipeline")
                .param("jobName", jobName))
                .andExpect(status().isOk())
                .andExpect(content().string("Jenkins job triggered!"));

        verify(jenkinsService).triggerJob(jobName);
    }

    @Test
    void triggerPipeline_shouldReturnInternalServerError_whenServiceThrowsException() throws Exception {
        String jobName = "nonExistentJob";
        // Предположим, что сервис выбрасывает RuntimeException, если джоба не найдена или другая ошибка
        doThrow(new RuntimeException("Trigger error")).when(jenkinsService).triggerJob(jobName);

        mockMvc.perform(post("/api/jenkins/trigger-pipeline")
                .param("jobName", jobName))
                .andExpect(status().isInternalServerError()); 
                // TODO: Уточнить ожидаемый статус

        verify(jenkinsService).triggerJob(jobName);
    }

    // --- Тесты для getPipelineStatus ---

    @Test
    void getPipelineStatus_shouldReturnOkWithStatus_whenJobNameIsValid() throws Exception {
        String jobName = "testJob";
        String expectedStatus = "SUCCESS";
        when(jenkinsService.getJobStatus(jobName)).thenReturn(expectedStatus);

        mockMvc.perform(get("/api/jenkins/pipeline-status")
                .param("jobName", jobName))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedStatus));

        verify(jenkinsService).getJobStatus(jobName);
    }

    @Test
    void getPipelineStatus_shouldReturnOkWithNull_whenStatusIsNull() throws Exception {
        String jobName = "testJobWithNullStatus";
        when(jenkinsService.getJobStatus(jobName)).thenReturn(null);

        mockMvc.perform(get("/api/jenkins/pipeline-status")
                        .param("jobName", jobName))
                .andExpect(status().isOk())
                .andExpect(content().string("")); // Если статус null, тело ответа будет пустым

        verify(jenkinsService).getJobStatus(jobName);
    }


    @Test
    void getPipelineStatus_shouldReturnInternalServerError_whenServiceThrowsException() throws Exception {
        String jobName = "jobCausingError";
        when(jenkinsService.getJobStatus(jobName)).thenThrow(new RuntimeException("Status retrieval error"));

        mockMvc.perform(get("/api/jenkins/pipeline-status")
                .param("jobName", jobName))
                .andExpect(status().isInternalServerError());
                 // TODO: Уточнить ожидаемый статус

        verify(jenkinsService).getJobStatus(jobName);
    }
    
    // --- Тест для ping ---

    @Test
    void ping_shouldReturnPong() throws Exception {
        mockMvc.perform(get("/api/jenkins/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }
} 