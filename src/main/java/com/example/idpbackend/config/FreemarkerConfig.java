package com.example.idpbackend.config;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@org.springframework.context.annotation.Configuration
public class FreemarkerConfig {

    @Bean
    @Primary
    public Configuration freemarkerConfiguration() throws IOException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setClassLoaderForTemplateLoading(
                Thread.currentThread().getContextClassLoader(), "templates"); // <- Указали путь
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return configuration;
    }
}
