package com.truyenchu.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class GitHubConfig {
    
    @Value("${github.token}")
    private String githubToken;
    
    @Value("${github.repository}")
    private String repository;
    
    @Value("${github.branch:main}")
    private String branch;
    
    @Value("${github.path:images}")
    private String imagePath;
    
    // Getters for validation
    public String getGithubToken() {
        return githubToken;
    }
    
    public String getRepository() {
        return repository;
    }
    
    public String getBranch() {
        return branch;
    }
    
    public String getImagePath() {
        return imagePath;
    }
} 