package com.example.idpbackend.controller;

import com.example.idpbackend.entity.Deploy;
import com.example.idpbackend.service.DeployService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureMockMvc
class DeployControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeployService deployService;

    @Test
    void deployFrontend_success() throws Exception {
        // Задаем параметры запроса
        int port = 8081;
        String jobName = "test-job";
        String repoUrl = "https://github.com/test/repo.git";
        String jenkinsfilePath = "Jenkinsfile";

        // Создаем ArgumentCaptor для объекта Deploy
        ArgumentCaptor<Deploy> deployCaptor = ArgumentCaptor.forClass(Deploy.class);

        // Настраиваем мок сервиса
        // Метод saveDeployInformation должен вызываться и возвращать Deploy
        when(deployService.saveDeployInformation(deployCaptor.capture())).thenReturn(null); // Используем captor
        // Метод deployFrontend также должен вызываться и ничего не возвращать (void)
        doNothing().when(deployService).deployFrontend(any(), eq(port));

        // Выполняем POST запрос
        mockMvc.perform(post("/api/deploy/frontend")
                        .param("port", String.valueOf(port))
                        .param("jobName", jobName)
                        .param("repoUrl", repoUrl)
                        .param("jenkinsfilePath", jenkinsfilePath)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Frontend развернут на порту: " + port + ". Информация о деплое сохранена."));

        // Проверяем, что методы сервиса были вызваны с ожидаемыми параметрами
        // verify(deployService).saveDeployInformation(any(Deploy.class)); // Старая проверка, заменяем на captor
        verify(deployService).deployFrontend(any(), eq(port)); // Проверяем вызов deployFrontend с любым Path и указанным портом

        // Получаем захваченный объект Deploy
        Deploy capturedDeploy = deployCaptor.getValue();

        // Проверяем поля захваченного объекта Deploy
        assertEquals(port, capturedDeploy.getPort());
        assertEquals(jobName, capturedDeploy.getJobName());
        assertEquals(repoUrl, capturedDeploy.getRepoUrl());
        assertEquals(jenkinsfilePath, capturedDeploy.getJenkinsfilePath());
    }
} 