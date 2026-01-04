package com.winestoreapp.service.impl;

import com.winestoreapp.service.ImageStorageService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class FileSystemImageStorageServiceImpl implements ImageStorageService {
    private static final String IMAGE_SAVE_PATH = "src/main/resources/static/images/wine/";
    private static final int CHARACTERS_LENGTH_CONTAINING_EXTENSION = 4;
    private static final int FIRST_POSITION = 0;

    @Override
    public String saveImage(String originalName, String suffix, MultipartFile file) {
        try {
            Path uploadPath = Path.of(IMAGE_SAVE_PATH);
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = generateUniqueFileName(suffix, Objects.requireNonNull(originalName));
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Image successfully saved: " + filePath);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error saving image: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteImage(String pictureLink) {
        if (pictureLink == null || pictureLink.isEmpty()) return;

        try {
            int lastIndexOfSlash = pictureLink.lastIndexOf("/");
            String fileName = pictureLink.substring(lastIndexOfSlash + 1);
            Path filePath = Paths.get(IMAGE_SAVE_PATH + fileName);

            Files.delete(filePath);
            log.info("Old image deleted: " + filePath);
        } catch (NoSuchFileException e) {
            log.warn("File to delete not found: " + pictureLink);
        } catch (IOException e) {
            log.error("Failed to delete file: " + pictureLink, e);
        }
    }

    private String generateUniqueFileName(String suffix, String fileName) {
        String uniquePart = "_" + suffix + "_" + System.currentTimeMillis();
        String baseName = fileName.substring(FIRST_POSITION,
                fileName.length() - CHARACTERS_LENGTH_CONTAINING_EXTENSION);
        String extension = fileName.substring(fileName.length() - CHARACTERS_LENGTH_CONTAINING_EXTENSION);
        return baseName + uniquePart + extension;
    }
}
