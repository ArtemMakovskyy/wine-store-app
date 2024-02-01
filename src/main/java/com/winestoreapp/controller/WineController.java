package com.winestoreapp.controller;

import com.winestoreapp.dto.wine.WineCreateRequestDto;
import com.winestoreapp.dto.wine.WineDto;
import com.winestoreapp.model.WineColor;
import com.winestoreapp.model.WineType;
import com.winestoreapp.service.impl.WineServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Wine management", description = "Endpoints to managing wines")
@RestController
@RequiredArgsConstructor
@RequestMapping("/wines")
public class WineController {
    private final WineServiceImpl wineService;

    @Operation(summary = "Load image from db.",
            description = "Load image from database")
    @GetMapping("/{id}/image/db")
    public ResponseEntity<Resource> getWinePictureDb(
            @PathVariable Long id) throws IOException {
        return wineService.getPictureByIdFromDb(id);
    }

    @Operation(summary = "Load image from path.",
            description = "Load image from path")
    @GetMapping("/{id}/image/path")
    public ResponseEntity<Resource> getWinePicturePath(
            @PathVariable Long id) throws IOException {
        System.out.println("getWinePicturePath");
        return wineService.getPictureByIdByPath(id);
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Creating a new Wine.",
            description = "Creating a new Wine with valid data")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WineDto createWine(
            @RequestBody @Valid WineCreateRequestDto createDto) {
        return wineService.add(createDto);
    }

    @Operation(summary = "Add an image into db.",
            description = "Add an image into database")
    @PatchMapping("/{id}/image/db")
    @ResponseStatus(HttpStatus.OK)
    public URL addImageByIdIntoDb(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws MalformedURLException {
        return wineService.addImage(id, file, true, false);
    }

    @Operation(summary = "Add an image into path.",
            description = "Add an image into path")
    @PatchMapping("/{id}/image/path")
    @ResponseStatus(HttpStatus.OK)
    public URL addImageByIdIntoPath(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws MalformedURLException {
        return wineService.addImage(id, file, false, true);
    }

    @GetMapping("/fill")
    public String createWines() throws MalformedURLException {
        wineService.add(
                new WineCreateRequestDto(
                        "Prince Trubetskoi Select Riesling",
                        new BigDecimal("188.99"),
                        "Riesling",
                        false,
                        WineType.DRY,
                        new BigDecimal("10.6"),
                        new BigDecimal("12.9"),
                        WineColor.WHITE,
                        "Deep red",
                        "delicate, balanced, round, with a fruity and honey aftertaste.",
                        "soft, generous, multifaceted, with hints of tropical fruits, notes of lychee and peach",
                        "Recommended for oriental dishes and fruits.",
                        "Vineyards stretch on the slopes of the Kakhovka reservoir. The unique terroir produces excellent wines. The harvest is harvested and sorted by hand. Fermentation of the wine, as well as maturation, takes place in tanks and is strictly controlled. Riesling is incredibly generous, multi-faceted and aromatic. Pleasant fruity and honey shades will give a truly vivid impression. Everyone likes this wine and is absolutely universal",
                        new URL("https://vino.ua/prints-trubetskoy-selekt-risling-beloe-sukhoe-ukraina/")
                ));
        wineService.add(
                new WineCreateRequestDto(
                        "Prince Trubetskoi Select Cabernet Sauvignon",
                        new BigDecimal("188.99"),
                        "Cabernet Sauvignon",
                        false,
                        WineType.DRY,
                        new BigDecimal("13"),
                        new BigDecimal("15"),
                        WineColor.RED,
                        null,
                        "Deep, enveloping, with notes of red berries, spice, with a long tart aftertaste",
                        "Deep, generous, with deep berry and chocolate notes, with hints of spice",
                        "Goes well with meat dishes, mature cheeses and stews",
                        "Cabernet Sauvignon grapes ripen on the slopes of the Kakhovka reservoir in the region of the Black Sea depression. Harvesting occurs manually when the berries have reached technical maturity...",
                        new URL("https://vino.ua/prints-trubetskoy-kaberne-sovinon-selekt-krasnoe-sukhoe-ukraina/")
                ));
        wineService.add(
                new WineCreateRequestDto(
                        "Prince Trubetskoi Select Malbec Prince Trubetskoi Select Cabernet Sauvignon",
                        new BigDecimal("188.99"),
                        "Malbec",
                        false,
                        WineType.DRY,
                        new BigDecimal("10.6"),
                        new BigDecimal("12.9"),
                        WineColor.RED,
                        null,
                        "bright harmonious, berry with a delicate aftertaste and round tannins",
                        "berry with a hint of milk chocolate with notes of black cherry, pomegranate, plum, raspberry, blackberry and blueberry",
                        "goes well with meat dishes - stewed and fried meat, roast beef, steaks.",
                        "The wine is made in a limited edition. Harvesting took place entirely by hand on a site with an area of 5 hectares and which stretches on the slopes of the Dnieper with a south-eastern exposure. In order to achieve the highest quality grapes, the volume of the harvest was limited. The soil of the terroir is southern weakly humus-accumulative medium loamy chernozem. The wine has a magnificent bright character. The generous aromatic bouquet reveals notes of red berries, raspberries, blackberries, blueberries, plums. This wine will be an excellent accompaniment to meat dishes, as well as for solo consumption",
                        new URL("https://vino.ua/prints-trubetskoy-selekt-malbek-krasnoe-sukhoe-ukraina/ new URL(")
                ));

        wineService.add(
                new WineCreateRequestDto(
                        "Prince Trubetskoi Select Sauvignon Blanc Prince Trubetskoi Select Malbec Prince Trubetskoi Select Cabernet Sauvignon,Prince Trubetskoi Select Cabernet Sauvignon",
                        new BigDecimal("188.99"),
                        "Sauvignon Blanc",
                        false,
                        WineType.DRY,
                        new BigDecimal("9.5"),
                        new BigDecimal("14"),
                        WineColor.WHITE,
                        null,
                        "rich, harmoniously combined with pleasant acidity",
                        "Rich, fresh, with aromas of green apples, black currant leaves and notes of meadow grass",
                        "Pairs well with salads, seafood, fish and white poultry dishes",
                        "The Sauvignon Blanc grapes are harvested and sorted by hand. Next comes cold maceration on the pulp for up to 6 hours. The fermentation process takes place in stainless steel tanks. Next, the wine is aged on fine yeast lees for two months, after which the wine rests in the bottle for another month before going on sale. Sauvignon Blanc wine has a fresh aromatic character. This wine will be an excellent choice for a hot summer day and get-togethers with friends.",
                        new URL("https://vino.ua/prints-trubetskoy-selekt-sovinon-blan-beloe-sukhoe-ukraina/ new URL(")
                ));
        wineService.add(
                new WineCreateRequestDto(
                        "Prince Trubetskoi Select Shiraz Prince Trubetskoi Select Sauvignon Blanc Prince Trubetskoi Select Malbec Prince Trubetskoi Select Cabernet Sauvignon,Prince Trubetskoi Select Cabernet Sauvignon, Prince Trubetskoi Select Malbec Prince Trubetskoi Select Cabernet Sauvignon",
                        new BigDecimal("188.99"),
                        "Shiraz",
                        false,
                        WineType.DRY,
                        new BigDecimal("13"),
                        new BigDecimal("15"),
                        WineColor.RED,
                        null,
                        "juicy, full-bodied wine with a pleasant, long aftertaste, with soft velvety tannins",
                        "rich, with notes of ripe plum and blackberry, as well as hints of smoke, spice, animalic tones and green nuances",
                        "goes well with baked lamb, grilled steak, oriental vegetables, vegetable salads and fish dishes",
                        "Plantings of shiraz grapes are located on the slope of the Kakhovka reservoir within the Black Sea depression of the East European Plain. The grapes are collected and sorted by hand. Cold infusion on the pulp for about 6 hours. The main fermentation is in stainless steel containers, aging on fine yeast lees for 2 months and resting for at least 1 month in the bottle. This creates a deep, rich wine with a vibrant berry profile as well as rich spice and animalic tones. This wine goes well with meat dishes and grilled dishes",
                        new URL("https://vino.ua/prints-trubetskoy-selekt-shiraz-krasnoe-sukhoe-ukraina/ new URL(")
                ));
        wineService.add(
                new WineCreateRequestDto(
                        "Prince Trubetskoi Select Pinot Blanc Prince Trubetskoi Select Shiraz Prince Trubetskoi Select Sauvignon Blanc Prince Trubetskoi Select Malbec Prince Trubetskoi Select Cabernet Sauvignon, Prince Trubetskoi Select Cabernet Sauvignon, Prince Trubetskoi Select Malbec Prince Trubetskoi Select Cabernet Sauvignon , Prince Trubetskoi Select Sauvignon Blanc Prince Trubetskoi Select Malbec Prince Trubetskoi Select Cabernet Sauvignon, Prince Trubetskoi Select Cabernet Sauvignon",
                        new BigDecimal("188.99"),
                        "Pinot Blanc",
                        false,
                        WineType.DRY,
                        new BigDecimal("10.6"),
                        new BigDecimal("12.9"),
                        WineColor.WHITE,
                        null,
                        "мягкий, деликатный, гармоничный с освежающей кислотностью",
                        "generous, varietal with notes of fresh fruit, peach and a hint of citrus",
                        "goes well with vegetable salads and fish dishes",
                        "Pinot Blanc berries are harvested and sorted entirely by hand. The grapes are quickly delivered to the plant, which allows for minimal contact of the berries with oxygen. Cold maceration lasts up to six hours. The fermentation process takes place in steel tanks. At the end of fermentation, the wine is aged on fine yeast lees for two months. Before going on sale, the wine rests for a month in the bottle. Pinot Blanc is an incredibly fresh and aromatic wine. A beautiful bouquet reveals notes of fruit, citrus, and peach. This is a universal wine, both for a large company and for solo consumption",
                        new URL("https://vino.ua/prints-trubetskoy-selekt-pino-blan-beloe-sukhoe-ukraina/ new URL(")
                ));
        return "wines have added";
    }
}
