package com.truyenchu.demo.controller;

import com.truyenchu.demo.dto.UploadResponseDTO;
import com.truyenchu.demo.service.GitHubService;
import com.truyenchu.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
public class UploadController {
    
    private final GitHubService githubService;
    private final UserService userService;
    
    /**
     * Upload a single image
     */
    @PostMapping("/image")
    public ResponseEntity<UploadResponseDTO> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new UploadResponseDTO(null, null, null, "File is empty", false));
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(new UploadResponseDTO(null, null, null, "File must be an image", false));
            }
            
            // Validate file size (max 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(new UploadResponseDTO(null, null, null, "File size must be less than 10MB", false));
            }
            
            // Upload to GitHub
            String githubUrl = githubService.uploadImageToGitHub(file);
            
            // Generate CDN URL
            String cdnUrl = githubService.generateJsDelivrUrl(githubUrl);
            
            UploadResponseDTO response = new UploadResponseDTO(
                    cdnUrl,
                    file.getOriginalFilename(),
                    githubUrl,
                    "Image uploaded successfully",
                    true
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error uploading image: {} - {}", file.getOriginalFilename(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new UploadResponseDTO(null, file.getOriginalFilename(), null, 
                            "Failed to upload image: " + e.getMessage(), false));
        }
    }
    
    /**
     * Upload multiple images
     */
    @PostMapping("/images")
    public ResponseEntity<List<UploadResponseDTO>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<UploadResponseDTO> responses = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                // Validate file
                if (file.isEmpty()) {
                    responses.add(new UploadResponseDTO(null, file.getOriginalFilename(), null, 
                            "File is empty", false));
                    continue;
                }
                
                // Validate file type
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    responses.add(new UploadResponseDTO(null, file.getOriginalFilename(), null, 
                            "File must be an image", false));
                    continue;
                }
                
                // Validate file size (max 10MB)
                if (file.getSize() > 10 * 1024 * 1024) {
                    responses.add(new UploadResponseDTO(null, file.getOriginalFilename(), null, 
                            "File size must be less than 10MB", false));
                    continue;
                }
                
                // Upload to GitHub
                String githubUrl = githubService.uploadImageToGitHub(file);
                
                // Generate CDN URL
                String cdnUrl = githubService.generateJsDelivrUrl(githubUrl);
                
                responses.add(new UploadResponseDTO(
                        cdnUrl,
                        file.getOriginalFilename(),
                        githubUrl,
                        "Image uploaded successfully",
                        true
                ));
                
            } catch (Exception e) {
                log.error("Error uploading image {}: {}", file.getOriginalFilename(), e.getMessage(), e);
                responses.add(new UploadResponseDTO(null, file.getOriginalFilename(), null, 
                        "Failed to upload image: " + e.getMessage(), false));
            }
        }
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Upload service is running");
    }
    
    /**
     * Upload avatar image
     */
    @PostMapping("/avatar")
    public ResponseEntity<UploadResponseDTO> uploadAvatar(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new UploadResponseDTO(null, null, null, "File is empty", false));
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(new UploadResponseDTO(null, null, null, "File must be an image", false));
            }
            
            // Validate file size (max 5MB for avatar)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(new UploadResponseDTO(null, null, null, "Avatar file size must be less than 5MB", false));
            }
            
            // Upload to GitHub avatar folder
            String githubUrl = githubService.uploadImageToGitHub(file, "avatar");
            
            // Generate CDN URL
            String cdnUrl = githubService.generateJsDelivrUrl(githubUrl);
            
            // Save avatar URL to database
            String username = authentication.getName();
            userService.updateAvatar(username, cdnUrl);
            
            UploadResponseDTO response = new UploadResponseDTO(
                    cdnUrl,
                    file.getOriginalFilename(),
                    githubUrl,
                    "Avatar uploaded successfully and saved to profile",
                    true
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error uploading avatar: {} - {}", file.getOriginalFilename(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new UploadResponseDTO(null, file.getOriginalFilename(), null, 
                            "Failed to upload avatar: " + e.getMessage(), false));
        }
    }
    
    /**
     * Upload banner profile image
     */
    @PostMapping("/banner")
    public ResponseEntity<UploadResponseDTO> uploadBanner(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new UploadResponseDTO(null, null, null, "File is empty", false));
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(new UploadResponseDTO(null, null, null, "File must be an image", false));
            }
            
            // Validate file size (max 10MB for banner)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(new UploadResponseDTO(null, null, null, "Banner file size must be less than 10MB", false));
            }
            
            // Upload to GitHub banner folder
            String githubUrl = githubService.uploadImageToGitHub(file, "bannerprofile");
            
            // Generate CDN URL
            String cdnUrl = githubService.generateJsDelivrUrl(githubUrl);
            
            // Save banner URL to database
            String username = authentication.getName();
            userService.updateBanner(username, cdnUrl);
            
            UploadResponseDTO response = new UploadResponseDTO(
                    cdnUrl,
                    file.getOriginalFilename(),
                    githubUrl,
                    "Banner uploaded successfully and saved to profile",
                    true
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error uploading banner: {} - {}", file.getOriginalFilename(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new UploadResponseDTO(null, file.getOriginalFilename(), null, 
                            "Failed to upload banner: " + e.getMessage(), false));
        }
    }
}
