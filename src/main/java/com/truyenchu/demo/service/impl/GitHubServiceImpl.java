package com.truyenchu.demo.service.impl;

import com.truyenchu.demo.service.GitHubService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@Slf4j
public class GitHubServiceImpl implements GitHubService {

    @Value("${github.token}")
    private String githubToken;

    @Value("${github.repository}")
    private String repository;

    @Value("${github.branch:main}")
    private String branch;

    @Value("${github.path:images}")
    private String imagePath;

    @Override
    public String uploadImageToGitHub(MultipartFile file) throws Exception {
        try {
            // Connect to GitHub
            GitHub github = new GitHubBuilder().withOAuthToken(githubToken).build();
            GHRepository repo = github.getRepository(repository);

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String randomString = RandomStringUtils.randomAlphanumeric(8);
            String newFilename = timestamp + "_" + randomString + "." + extension;

            // Create file path
            String filePath = imagePath + "/" + newFilename;

            // Read file content as binary
            byte[] fileContent = file.getBytes();

            // Create the file directly in the repository using binary content
            repo.createContent()
                    .path(filePath)
                    .message("Upload image: " + newFilename)
                    .content(fileContent) // Use binary content instead of Base64
                    .branch(branch)
                    .commit();

            // Return the GitHub raw URL
            return "https://raw.githubusercontent.com/" + repository + "/" + branch + "/" + filePath;

        } catch (IOException e) {
            log.error("Error uploading image to GitHub: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload image to GitHub", e);
        }
    }

    @Override
    public String uploadImageToGitHub(MultipartFile file, String folder) throws Exception {
        try {
            // Connect to GitHub
            GitHub github = new GitHubBuilder().withOAuthToken(githubToken).build();
            GHRepository repo = github.getRepository(repository);

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String randomString = RandomStringUtils.randomAlphanumeric(8);
            String newFilename = timestamp + "_" + randomString + "." + extension;

            // Create file path with specified folder
            String filePath = folder + "/" + newFilename;

            // Read file content as binary
            byte[] fileContent = file.getBytes();

            // Create the file directly in the repository using binary content
            repo.createContent()
                    .path(filePath)
                    .message("Upload image: " + newFilename)
                    .content(fileContent) // Use binary content instead of Base64
                    .branch(branch)
                    .commit();

            // Return the GitHub raw URL
            return "https://raw.githubusercontent.com/" + repository + "/" + branch + "/" + filePath;

        } catch (IOException e) {
            log.error("Error uploading image to GitHub: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload image to GitHub", e);
        }
    }

    @Override
    public String generateJsDelivrUrl(String githubUrl) {
        // Convert GitHub raw URL to jsDelivr CDN URL
        // Format: https://raw.githubusercontent.com/username/repo/branch/path
        // To: https://cdn.jsdelivr.net/gh/username/repo@branch/path
        
        if (githubUrl.startsWith("https://raw.githubusercontent.com/")) {
            String path = githubUrl.replace("https://raw.githubusercontent.com/", "");
            String[] parts = path.split("/", 3);
            
            if (parts.length >= 3) {
                String username = parts[0];
                String repoName = parts[1];
                String filePath = parts[2];
                
                // Remove the branch name from filePath if it's duplicated
                if (filePath.startsWith(branch + "/")) {
                    filePath = filePath.substring(branch.length() + 1);
                }
                
                return "https://cdn.jsdelivr.net/gh/" + username + "/" + repoName + "@" + branch + "/" + filePath;
            }
        }
        
        return githubUrl; // Return original URL if conversion fails
    }
} 