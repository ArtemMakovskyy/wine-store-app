package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.WineMapper;
import com.winestoreapp.dto.wine.WineCreateRequestDto;
import com.winestoreapp.dto.wine.WineDto;
import com.winestoreapp.exception.EmptyDataException;
import com.winestoreapp.model.Wine;
import com.winestoreapp.repository.WineRepository;
import com.winestoreapp.service.WineService;
import jakarta.persistence.EntityNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class WineServiceImpl implements WineService {
    private static final String SAVE_PATH = "upload/pictures/wine/";
    private final WineRepository wineRepository;
    private final WineMapper wineMapper;
    // TODO: 07.02.2024 dell it if not use
    @Value("${base.url}")
    private URL baseUrl;

    @Override
    public WineDto add(WineCreateRequestDto createDto) {
        // TODO: 06.02.2024 save info
        //if vendor code null create it
        //if shortname null  code null create it
        final Wine wine = wineMapper.toEntity(createDto);
        // TODO: 06.02.2024 checkImageForLoading think about saving link
        final Wine savedWine = wineRepository.save(wine);
        // TODO: 06.02.2024 save file
        // TODO: 06.02.2024 result
        return wineMapper.toDto(savedWine);
    }

    @Override
    public URL updateImage(
            Long id,
            MultipartFile multipartFile,
            boolean addToDb)
            throws MalformedURLException {
        // TODO: 06.02.2024 think about exceptions and constants
        if (multipartFile.isEmpty()) {
            throw new EmptyDataException("Please select the file");
        }
        Wine wineFromDb = wineRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find wine by id " + id));

        byte[] imageBytes = null;
        try {
            imageBytes = multipartFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error getting bytes from image");
        }
        if (addToDb) {
            wineFromDb.setPicture(imageBytes);
            wineFromDb.setPictureLink(new URL(baseUrl + "api/wines/" + id + "/image/db"));
            log.info("Image " + multipartFile.getOriginalFilename()
                    + " successfully added into database");
            wineRepository.save(wineFromDb);
        }
        if (!addToDb) {
            String filePath = SAVE_PATH + id + getFileExtension(multipartFile);
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(imageBytes);
                wineFromDb.setPictureLink(new URL(baseUrl + "api/wines/" + id + "/image/path"));
                wineRepository.save(wineFromDb);
                log.info("Image successfully added into file " + filePath);
            } catch (IOException e) {
                throw new RuntimeException("Error image saving: " + e);
            }
        }
        return wineFromDb.getPictureLink();
    }

    @Override
    public List<WineDto> findAll() {
        return wineRepository.findAll().stream()
                .map(wineMapper::toDto)
                .toList();
    }

    @Override
    public WineDto findById(Long id) {
        return wineMapper.toDto(wineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find wine by id: " + id)));
    }

    @Override
    public boolean isDeleteById(Long id) {
        if (!wineRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find wine by id: " + id);
        }
        wineRepository.deleteById(id);
        return true;
    }

    // TODO: 06.02.2024 old code
    public String getFileExtension(MultipartFile multipartFile) {
        // Получаем оригинальное имя файла
        String originalFilename = multipartFile.getOriginalFilename();

        if (originalFilename != null && originalFilename.contains(".")) {
            // Извлекаем расширение из имени файла
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            // Если оригинальное имя файла не содержит расширение
            return "";
        }
    }

    public Wine getWineById(Long id) {
        return wineRepository.findById(id).orElseThrow();
    }

    public ResponseEntity<Resource> getPictureByIdFromDb(Long id) throws IOException {
        Wine wine = getWineById(id);
        byte[] picture = wine.getPicture();

        ByteArrayResource byteArrayResource = new ByteArrayResource(picture);

        // TODO: 01.02.2024 whi this need
        HttpHeaders headers = new HttpHeaders();
        // TODO: 02.02.2024 fix it
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + id + "jpg");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.IMAGE_JPEG)
                .body(byteArrayResource);
    }

    public ResponseEntity<Resource> getPictureByIdByPath(Long id) {
        String imagePath = "upload/pictures/wine/" + id.toString();
        String extension = ".jpg";
        byte[] picture = new byte[0];
        try {
            picture = loadImageAsByteArray(imagePath + extension);
            // TODO: 02.02.2024 how to implement it correctly
        } catch (IOException e) {
            try {
                extension = ".png";
                picture = loadImageAsByteArray(imagePath + extension);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        ByteArrayResource byteArrayResource = new ByteArrayResource(picture);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + id + extension);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.IMAGE_JPEG)
                .body(byteArrayResource);
    }

    private static byte[] loadImageAsByteArray(String imagePath) throws IOException {
        Path path = Paths.get(imagePath);
        return Files.readAllBytes(path);
    }
}
