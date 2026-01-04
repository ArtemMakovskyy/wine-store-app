package com.winestoreapp.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    String saveImage(String originalName, String suffix, MultipartFile file);
    void deleteImage(String pictureLink);
}
