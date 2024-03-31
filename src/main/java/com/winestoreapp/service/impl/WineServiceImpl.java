package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.WineMapper;
import com.winestoreapp.dto.wine.WineCreateRequestDto;
import com.winestoreapp.dto.wine.WineDto;
import com.winestoreapp.exception.EntityNotFoundException;
import com.winestoreapp.model.Wine;
import com.winestoreapp.repository.WineRepository;
import com.winestoreapp.service.WineService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class WineServiceImpl implements WineService {
    private static final String IMAGE_PATH = "api/images/wine/";
    private static final String IDENTIFIER_FOR_FIRST_IMAGE = "a";
    private static final String IDENTIFIER_FOR_SECOND_IMAGE = "b";
    private static final int CHARACTERS_LENGTH_CONTAINING_EXTENSION = 4;
    private static final int FIRST_POSITION = 0;
    private static final String IMAGE_SAVE_PATH = "src/main/resources/static/images/wine/";
    private final WineRepository wineRepository;
    private final WineMapper wineMapper;

    @Override
    public WineDto add(WineCreateRequestDto createDto) {
        final Wine wine = wineMapper.toEntity(createDto);
        final Wine savedWine = wineRepository.save(wine);
        return wineMapper.toDto(savedWine);
    }

    @Override
    public WineDto updateImage(Long id, MultipartFile imageA, MultipartFile imageB) {
        Wine wineFromDb = wineRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find wine by id " + id));

        final String imageNameA = saveImage(wineFromDb, imageA, IDENTIFIER_FOR_FIRST_IMAGE);
        final String imageNameB = saveImage(wineFromDb, imageB, IDENTIFIER_FOR_SECOND_IMAGE);

        wineFromDb.setPictureLink(IMAGE_PATH + imageNameA);
        wineFromDb.setPictureLink2(IMAGE_PATH + imageNameB);
        wineRepository.save(wineFromDb);

        return wineMapper.toDto(wineRepository.save(wineFromDb));
    }

    private String saveImage(Wine wine, MultipartFile image, String uniqueIdentificationSymbol) {
        try {
            Path uploadPath = Path.of(IMAGE_SAVE_PATH);
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            final String newFileNameForFirstLink = generateUniqueFileName(
                    uniqueIdentificationSymbol,
                    Objects.requireNonNull(image.getOriginalFilename()));
            Path filePath = uploadPath.resolve(newFileNameForFirstLink);

            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            deleteOldImagesIfExist(wine.getPictureLink());
            deleteOldImagesIfExist(wine.getPictureLink2());

            log.info("Image successfully added into file " + filePath);
            return newFileNameForFirstLink;
        } catch (IOException e) {
            throw new RuntimeException("Error saving image: " + e.getMessage(), e);
        }
    }

    private boolean deleteOldImagesIfExist(String pictureLink) {
        if (pictureLink != null) {
            int lastIndexOfSlash = pictureLink.lastIndexOf("/");
            String fileToDelete = pictureLink.substring(lastIndexOfSlash + 1);
            Path linkFromFileToDelete = Paths.get(IMAGE_SAVE_PATH + fileToDelete);
            try {
                Files.delete(linkFromFileToDelete);
                log.info("Old image file successfully deleted: " + linkFromFileToDelete);
                return true;
            } catch (NoSuchFileException e) {
                log.warn("File does not exist at path: " + linkFromFileToDelete);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file at path: "
                        + linkFromFileToDelete, e);
            }
        }
        return false;
    }

    private String generateUniqueFileName(String identificationSymbol, String fileName) {
        String uniqueIdentification = "_"
                + identificationSymbol
                + System.currentTimeMillis();
        String originalFileNameWithoutExtension = fileName.substring(
                FIRST_POSITION, fileName.length() - CHARACTERS_LENGTH_CONTAINING_EXTENSION);
        String fileExtension = fileName.substring(
                fileName.length() - CHARACTERS_LENGTH_CONTAINING_EXTENSION);
        return originalFileNameWithoutExtension
                + uniqueIdentification + System.currentTimeMillis() + fileExtension;
    }

    @Override
    public List<WineDto> findAll(Pageable pageable) {
        return wineRepository.findAll(pageable).stream()
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
}
