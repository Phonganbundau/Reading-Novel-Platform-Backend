package com.truyenchu.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponseDTO {
    private String cdnUrl;
    private String originalFileName;
    private String githubUrl;
    private String message;
    private boolean success;
} 