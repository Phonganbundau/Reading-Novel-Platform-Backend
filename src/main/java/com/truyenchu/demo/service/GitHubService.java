package com.truyenchu.demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface GitHubService {
    String uploadImageToGitHub(MultipartFile file) throws Exception;
    String uploadImageToGitHub(MultipartFile file, String folder) throws Exception;
    String generateJsDelivrUrl(String githubUrl);
} 