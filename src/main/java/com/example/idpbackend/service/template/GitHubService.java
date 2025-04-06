package com.example.idpbackend.service.template;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;

import java.io.IOException;

public interface GitHubService {
    GHRepository createRepositoryFromTemplate(String organizationName, String newRepoName, String templateRepoName) throws IOException;
    GHTree getRepositoryTree(String repositoryName) throws IOException;
    byte[] getFileContent(String repositoryName, String filePath) throws IOException;
    void createOrUpdateFile(String repositoryName, String filePath, String content, String commitMessage) throws IOException;
}
