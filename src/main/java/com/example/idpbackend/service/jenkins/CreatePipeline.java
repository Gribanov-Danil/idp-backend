package com.example.idpbackend.service.jenkins;

import org.springframework.stereotype.Service;
// import jenkins.model.Jenkins;
// import hudson.model.FreeStyleProject;
// import hudson.model.Job;
// import hudson.model.Item;
// import hudson.model.Descriptor;
// import hudson.tasks.Shell;

@Service
public class CreatePipeline {

  public void createPipeline(String pipelineName, String pipelineConfig) {
    // try {
    //   Jenkins jenkins = Jenkins.getInstance();
    //   FreeStyleProject project = jenkins.createProject(FreeStyleProject.class, pipelineName);

    //   // Настройка пайплайна
    //   project.getBuildersList().add(new Shell(pipelineConfig));
      
    //   // Сохранение проекта
    //   project.save();
    // } catch (Exception e) {
    //   e.printStackTrace();
    //   // Обработка ошибок
    // }
  }
}
