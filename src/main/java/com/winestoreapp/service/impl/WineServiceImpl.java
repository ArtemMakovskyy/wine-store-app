package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.WineMapper;
import com.winestoreapp.dto.wine.WineCreateRequestDto;
import com.winestoreapp.dto.wine.WineDto;
import com.winestoreapp.exception.EmptyDataException;
import com.winestoreapp.exception.EntityNotFoundException;
import com.winestoreapp.model.Wine;
import com.winestoreapp.model.WineColor;
import com.winestoreapp.model.WineType;
import com.winestoreapp.repository.WineRepository;
import com.winestoreapp.service.WineService;
import jakarta.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
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
import org.springframework.data.domain.Pageable;
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
        final Wine wine = wineMapper.toEntity(createDto);
        // TODO: 06.02.2024 checkImageForLoading think about saving link
        final Wine savedWine = wineRepository.save(wine);
        // TODO: 06.02.2024 save file
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

    public String getFileExtension(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            return "";
        }
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

    @PostConstruct
    public void addWines() throws MalformedURLException {
        log.info("into Add wines");
        if (wineRepository.count() <= 0) {
            add(new WineCreateRequestDto(
                    "vendorCode",
                    "Limeted Edition Vine",
                    "Prince Trubetskoi Select Riesling",
                    "shortName",
                    new BigDecimal("188.99"),
                    "Riesling",
                    false,
                    WineType.DRY,
                    new BigDecimal("10.6"),
                    new BigDecimal("12.9"),
                    WineColor.WHITE,
                    "Deep red",
                    "delicate, balanced, round, with a fruity and honey aftertaste.",
                    "soft, generous, multifaceted, with hints of tropical ",
                    "Recommended for oriental dishes and fruits.",
                    """
                            Vineyards stretch on the slopes of the Kakhovka reservoir.
                            The unique terroir produces excellent wines.
                            The harvest is harvested and sorted by hand.
                            Fermentation of the wine, as well as maturation,
                            takes place in tanks and is strictly controlled.
                            Riesling is incredibly generous, multi-faceted and aromatic.
                            Pleasant fruity and honey shades will give a truly vivid impression.
                            Everyone likes this wine and is absolutely universal""",
                    new byte[1]
            ));
            add(new WineCreateRequestDto(
                    "vendorCode",
                    "Limeted Edition Vine",
                    "Prince Trubetskoi Select Cabernet Sauvignon",
                    "shortName",
                    new BigDecimal("160.50"),
                    "Cabernet Sauvignon",
                    false,
                    WineType.DRY,
                    new BigDecimal("13"),
                    new BigDecimal("15"),
                    WineColor.RED,
                    "Deep red",
                    "deep, enveloping, with notes of red berries, spice, "
                            + "with a long tart aftertaste",
                    "deep, generous, with deep berry and chocolate notes, with hints of spice",
                    "goes well with meat dishes, mature cheeses and stews",
                    """
                            Cabernet Sauvignon grapes ripen on the slopes of the Kakhovka 
                            reservoir in the region of the Black Sea depression. Harvesting 
                            occurs manually when the berries have reached technical maturity. 
                            At all stages of production, all processes and temperatures are 
                            strictly controlled. The Cabernet Sauvignon wine is deep, 
                            generous and rich. The multifaceted aroma reveals juicy notes of 
                            red berries, black currants, cherries, spices, animal shades, and 
                            the deep taste delights with velvety tannins. The wine will be an 
                            excellent accompaniment to meat dishes and grilled dishes""",
                    new byte[1]
            ));

            add(new WineCreateRequestDto(
                    "vendorCode",
                    "Limeted Edition Vine",
                    "Prince Trubetskoi Select Malbec",
                    "shortName",
                    new BigDecimal("170.50"),
                    "Malbec",
                    false,
                    WineType.DRY,
                    new BigDecimal("10.6"),
                    new BigDecimal("12.9"),
                    WineColor.RED,
                    "Deep red",
                    "bright harmonious, berry with a delicate aftertaste and round tannins",
                    "berry with a hint of milk chocolate with notes of black cherry, "
                            + "pomegranate, plum, raspberry, blackberry and blueberry",
                    "goes well with meat dishes - stewed and fried meat, roast beef, steaks.",
                    """
                            The wine is made in a limited edition. Harvesting took place entirely 
                            by hand on a site with an area of hectares and which stretches on the 
                            slopes of the Dnieper with a south-eastern exposure. In order to 
                            achieve the highest quality grapes, the volume of the harvest was 
                            limited. The soil of the terroir is southern weakly 
                            humus-accumulative medium loamy chernozem. The wine has a magnificent 
                            bright character. The generous aromatic bouquet reveals notes of red 
                            berries, raspberries, blackberries, blueberries, plums. This wine 
                            will be an excellent accompaniment to meat dishes, as well as for 
                            solo consumption""",
                    new byte[1]
            ));
        }
    }
}
