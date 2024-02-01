package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.WineMapper;
import com.winestoreapp.dto.wine.WineCreateRequestDto;
import com.winestoreapp.dto.wine.WineDto;
import com.winestoreapp.exception.EmptyDataException;
import com.winestoreapp.model.Wine;
import com.winestoreapp.repository.WineRepository;
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
public class WineServiceImpl implements WineService{
    private static final int NUMBER_OF_SIMBOLS_AFTER_COMMA = 2;
    private static final String SAVE_PATH = "upload/pictures/wine/";
    private final WineRepository wineRepository;
    private final WineMapper wineMapper;
    @Value("${limiter.number.of.recorded.ratings}")
    private int limiterOnTheNumberOfRecordedRatings;

    @Override
    public WineDto add(WineCreateRequestDto createDto) {
        final Wine savedWine = wineRepository.save(wineMapper.toEntity(createDto));
        System.out.println(savedWine.getId());
        return wineMapper.toDto(savedWine);
    }

    public URL addImage(
            Long id,
            MultipartFile multipartFile,
            boolean addToDb,
            boolean addToPath)
            throws MalformedURLException {
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
        /// TODO: 02.02.2024 customize local host
        if (addToDb) {
            wineFromDb.setPicture(imageBytes);
            wineFromDb.setPictureLink(new URL("http://localhost:8080/api/wines/" + id + "/image/db"));
            log.info("Image " + multipartFile.getOriginalFilename()
                    + " successfully added into database");
            wineRepository.save(wineFromDb);
        }
        // TODO: 02.02.2024 check it
        if (addToPath) {
            String filePath = SAVE_PATH + id + getFileExtension(multipartFile);
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(imageBytes);
                wineFromDb.setPictureLink(new URL("http://localhost:8080/api/wines/" + id + "/image/path"));
                wineRepository.save(wineFromDb);
                log.info("Image successfully added into file " + filePath);
            } catch (IOException e) {
                throw new RuntimeException("Error image saving: " + e);
            }
        }
        return wineFromDb.getPictureLink();
    }

        //    public void addRating(Long wineId, Integer rating) {
        //        final Wine wine = wineRepository.findById(wineId)
        //                .orElseThrow(() -> new RuntimeException(
        //                "Can't get wine by id " + wineId));
        //        if (wine.getRatings().isEmpty()) {
        //            List<Integer> ratings = new ArrayList<>();
        //            wine.setRatings(ratings);
        //        }
        //        LinkedList<Integer> ratings = new LinkedList<>(wine.getRatings());
        //        if (ratings.size() >= limiterOnTheNumberOfRecordedRatings) {
        //            ratings.removeFirst();
        //        }
        //        ratings.add(rating);
        //        final BigDecimal averageRatingScore =
        //                BigDecimal.valueOf(
        //                        calculateAverageRatingScore(ratings)
        //                ).setScale(NUMBER_OF_SIMBOLS_AFTER_COMMA, RoundingMode.HALF_UP);
        //        wine.setAverageRatingScore(averageRatingScore);
        //        wine.setRatings(new ArrayList<>(ratings));
        //        Wine save = wineRepository.save(wine);
        //        System.out.println(save);
        //    }

    private double calculateAverageRatingScore(List<Integer> ratings) {
        final double numberOfRatingsAsPercentage
                = ratings.size() / (double) limiterOnTheNumberOfRecordedRatings;
        return ratings.stream()
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.00) + numberOfRatingsAsPercentage;
    }

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
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=originFileName.jpg");

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
