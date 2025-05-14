package com.example.idpbackend.service;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Сервис для загрузки артефактов.
 */
public interface ArtifactDownloaderService {

    /**
     * Загружает артефакт по имени задачи и имени файла артефакта.
     *
     * @param jobName          имя задачи (например, Jenkins job)
     * @param artifactFileName имя файла артефакта (например, "build.zip")
     * @return путь к загруженному временному файлу артефакта
     * @throws IOException если происходит ошибка во время загрузки
     */
    Path downloadArtifact(String jobName, String artifactFileName) throws IOException;
} 