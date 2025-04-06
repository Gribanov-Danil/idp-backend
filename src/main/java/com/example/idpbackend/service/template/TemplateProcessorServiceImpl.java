package com.example.idpbackend.service.template;

import com.example.idpbackend.dto.template.RepositoryRequest;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GHTreeEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Primary
public class TemplateProcessorServiceImpl implements TemplateProcessorService {

    private final GitHubService gitHubService;
    private final Configuration freemarkerConfig;
    private static final Logger logger = LoggerFactory.getLogger(TemplateProcessorServiceImpl.class);
    private final List<String> warnings = new ArrayList<>();

    public TemplateProcessorServiceImpl(GitHubService gitHubService, Configuration freemarkerConfig) {
        this.gitHubService = gitHubService;
        this.freemarkerConfig = freemarkerConfig;
    }

    @Override
    public String processAndCreateRepository(RepositoryRequest request) throws Exception {
        warnings.clear();

        String organizationName = extractOrganizationName(request.getOrganizationUrl());
        String templateRepoFullName = extractRepositoryName(request.getTemplateRepositoryUrl());

        GHRepository newRepository = gitHubService.createRepositoryFromTemplate(
                organizationName,
                request.getNewRepositoryName(),
                templateRepoFullName);

        Map<String, Object> context = new HashMap<>(request.getVariables());
        if (request.getConditions() != null) {
            request.getConditions().forEach((key, value) -> {
                context.put(key, String.valueOf(value));
            });
        }

        processRepositoryFiles(newRepository.getFullName(), templateRepoFullName, context);

        if (!warnings.isEmpty()) {
            logger.info("Обработка шаблонов завершена с предупреждениями: {}", warnings);
        }

        return newRepository.getHtmlUrl().toString();
    }

    private void processRepositoryFiles(String targetRepoName, String templateRepoFullName, Map<String, Object> context)
            throws IOException, TemplateException {

        GHTree tree = gitHubService.getRepositoryTree(templateRepoFullName);

        for (GHTreeEntry entry : tree.getTree()) {
            if ("blob".equals(entry.getType())) {
                processFile(templateRepoFullName, targetRepoName, entry.getPath(), context);
            }
        }
    }

    private void processFile(String templateRepoName, String targetRepoName, String filePath, Map<String, Object> context)
            throws IOException {

        byte[] fileContentBytes = gitHubService.getFileContent(templateRepoName, filePath);
        String originalContent = new String(fileContentBytes, StandardCharsets.UTF_8);
        String processedContent;

        try {
            processedContent = processWithFreemarker(originalContent, context);
        } catch (Exception e) {
            logger.warn("Не удалось обработать шаблон для файла '{}': {}. Сохраняем оригинал.", filePath, e.getMessage());
            processedContent = originalContent;
            warnings.add("Файл '" + filePath + "' не шаблонизирован: " + e.getMessage());
        }

        gitHubService.createOrUpdateFile(
                targetRepoName,
                filePath,
                processedContent,
                "Добавление файла из шаблона"
        );
    }

    private String processWithFreemarker(String content, Map<String, Object> context) throws IOException, TemplateException {
        Template template = new Template("temp", new StringReader(content), freemarkerConfig);
        StringWriter writer = new StringWriter();
        template.process(context, writer);
        return writer.toString();
    }

    private String extractOrganizationName(String organizationUrl) {
        return organizationUrl.substring(organizationUrl.lastIndexOf('/') + 1);
    }

    private String extractRepositoryName(String repositoryUrl) {
        try {
            URI uri = new URI(repositoryUrl);
            String path = uri.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return path;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid repository URL: " + repositoryUrl, e);
        }
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
