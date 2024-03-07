package com.winestoreapp.service.impl;

import com.winestoreapp.dto.mapper.WineMapper;
import com.winestoreapp.dto.wine.WineCreateRequestDto;
import com.winestoreapp.dto.wine.WineDto;
import com.winestoreapp.exception.EntityNotFoundException;
import com.winestoreapp.model.Wine;
import com.winestoreapp.model.WineColor;
import com.winestoreapp.model.WineType;
import com.winestoreapp.repository.WineRepository;
import com.winestoreapp.service.WineService;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class WineServiceImpl implements WineService {
    private static final String IMAGE_PATH = "api/images/wine/";
    private static final String IMAGE_SAVE_PATH = "src/main/resources/static/images/wine/";
    private final WineRepository wineRepository;
    private final WineMapper wineMapper;
    // TODO: 07.02.2024 dell it if not use
    @Value("${hosting.url}")
    private URL hostingUrl;

    @Override
    public WineDto add(WineCreateRequestDto createDto) {
        final Wine wine = wineMapper.toEntity(createDto);
        // TODO: 06.02.2024 checkImageForLoading think about saving link
        final Wine savedWine = wineRepository.save(wine);
        // TODO: 06.02.2024 save file
        return wineMapper.toDto(savedWine);
    }

    @Override
    public WineDto updateImage(Long id, MultipartFile imageA, MultipartFile imageB) {
        Wine wineFromDb = wineRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find wine by id " + id));

        final String imageNameA = saveImage(wineFromDb, imageA, "a");
        final String imageNameB = saveImage(wineFromDb, imageB, "b");

        wineFromDb.setPictureLink(IMAGE_PATH + imageNameA);
        wineFromDb.setPictureLink2(IMAGE_PATH + imageNameB);
        wineRepository.save(wineFromDb);

        return wineMapper.toDto(wineRepository.save(wineFromDb));
    }

    private String saveImage(Wine wine, MultipartFile image, String uniqueIdentificatorSymbol) {
        try {
            Path uploadPath = Path.of(IMAGE_SAVE_PATH);
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            final String newFileNameForFirstLink = generateUniqueFileName(
                    uniqueIdentificatorSymbol, image.getOriginalFilename());
            Path filePath = uploadPath.resolve(newFileNameForFirstLink);

            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            deleteOldImageIfExist(wine, uniqueIdentificatorSymbol);

            log.info("Image successfully added into file " + filePath);
            return newFileNameForFirstLink;
        } catch (IOException e) {
            throw new RuntimeException("Error saving image: " + e.getMessage(), e);
        }
    }

    private void deleteOldImageIfExist(Wine wine, String uniqueIdentificatorSymbol) {
        if (uniqueIdentificatorSymbol.equals("a")) {
            if (wine.getPictureLink() != null) {
                String existingLink = wine.getPictureLink();
                int lastIndexOfSlash = existingLink.lastIndexOf("/");
                String fileToDelete = existingLink.substring(lastIndexOfSlash + 1);
                Path linkFromFileToDelete = Paths.get(IMAGE_SAVE_PATH + fileToDelete);
                try {
                    Files.delete(linkFromFileToDelete);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                log.info("Old image file successfully deleted: " + linkFromFileToDelete);
            }
        }
        if (uniqueIdentificatorSymbol.equals("b")) {
            if (wine.getPictureLink2() != null) {
                String existingLink = wine.getPictureLink2();
                int lastIndexOfSlash = existingLink.lastIndexOf("/");
                String fileToDelete = existingLink.substring(lastIndexOfSlash + 1);
                Path linkFromFileToDelete = Paths.get(IMAGE_SAVE_PATH + fileToDelete);
                try {
                    Files.delete(linkFromFileToDelete);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                log.info("Old image file successfully deleted: " + linkFromFileToDelete);
            }
        }
    }

    private String generateUniqueFileName(String uniqueIdentificatorSymbol, String fileName) {
        String uniqueIdentificator = "_" + uniqueIdentificatorSymbol + System.currentTimeMillis();
        String originalFileNameWithoutExtension = fileName.substring(0, fileName.length() - 4);
        String fileExtension = fileName.substring(fileName.length() - 4);
        return originalFileNameWithoutExtension
                + uniqueIdentificator + System.currentTimeMillis() + fileExtension;
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

    @PostConstruct
    public void addWines() throws IOException {
        log.info("Add wines");

        if (wineRepository.count() == 0) {
            add(
                    new WineCreateRequestDto(
                            "MRD2019",
                            "Select",
                            null,
                            "Prince Trubetskoi Select Riesling",
                            "Riesling",
                            new BigDecimal("870"),
                            "Riesling",
                            false,
                            WineType.DRY,
                            new BigDecimal("10.6"),
                            new BigDecimal("12.9"),
                            WineColor.WHITE,
                            "citric",
                            "delicate, balanced, round, with a fruity and honey aftertaste.",
                            """
                                    soft, generous, multifaceted, with hints of tropical fruits,
                                    notes of lychee and peach""",
                            "Recommended for oriental dishes and fruits.",
                            """
                                    Vineyards stretch on the slopes of the Kakhovka reservoir.
                                    The unique terroir produces excellent wines. The harvest is
                                    harvested and sorted by hand. Fermentation of the wine, as
                                    well as maturation, takes place in tanks and is strictly
                                    controlled. Riesling is incredibly generous, multi-faceted
                                    and aromatic. Pleasant fruity and honey shades will give
                                    a truly vivid impression. Everyone likes this wine and
                                    is absolutely universal""",
                            IMAGE_PATH + "Riesling-White-Dry.png",
                            IMAGE_PATH + "Riesling-White-Dry3.png"
                    ));
            add(
                    new WineCreateRequestDto(
                            "MRD2020",
                            "Select",
                            null,
                            "Prince Trubetskoi Select Cabernet Sauvignon",
                            "Cabernet Sauvignon",
                            new BigDecimal("880"),
                            "Cabernet Sauvignon",
                            false,
                            WineType.DRY,
                            new BigDecimal("13"),
                            new BigDecimal("15"),
                            WineColor.RED,
                            "dark burgundy",
                            """
                                    deep, enveloping, with notes of red berries,
                                    spice, with a long tart aftertaste""",
                            """
                                    deep, generous, with deep berry and chocolate notes,
                                    with hints of spice""",
                            "goes well with meat dishes, mature cheeses and stews",
                            """
                                    Cabernet Sauvignon grapes ripen on the slopes of the Kakhovka
                                     reservoir in the region of the Black Sea depression.
                                     Harvesting occurs manually when the berries have reached
                                     technical maturity. At all stages of production, all
                                     processes and temperatures are strictly controlled.
                                     The Cabernet Sauvignon wine is deep, generous and rich.
                                     The multifaceted aroma reveals juicy notes of red berries,
                                     black currants, cherries, spices, animal shades, and the
                                     deep taste delights with velvety tannins. The wine will
                                     be an excellent accompaniment to meat dishes and grilled
                                     dishes""",
                            IMAGE_PATH + "Cabernet-Sauvignon-Red-Dry.png",
                            IMAGE_PATH + "Cabernet-Sauvignon-Red-Dry3.png"
                    ));
            add(
                    new WineCreateRequestDto(
                            "MRD2021",
                            "Select",
                            "Limeted Edition Vine",
                            "Prince Trubetskoi Select Malbec",
                            "Malbec",
                            new BigDecimal("870"),
                            "Malbec",
                            false,
                            WineType.DRY,
                            new BigDecimal("10.60"),
                            new BigDecimal("12.90"),
                            WineColor.WHITE,
                            "deep rich with ruby tint",
                            "bright harmonious, berry with a "
                                    + "delicate aftertaste and round tannins",
                            """
                                    berry with a hint of milk chocolate with
                                    notes of black cherry, pomegranate, plum,
                                    raspberry, blackberry and blueberry""",
                            "goes well with meat dishes - stewed "
                                    + "and fried meat, roast beef, steaks.",
                            """
                                    The wine is made in a limited edition. Harvesting took
                                    place entirely by hand on a site with an area of 5 hectares
                                    and which stretches on the slopes of the Dnieper with a
                                    south-eastern exposure. In order to achieve the highest
                                    quality grapes, the volume of the harvest was limited.
                                    The soil of the terroir is southern weakly humus-accumulative
                                    medium loamy chernozem. The wine has a magnificent bright
                                    character. The generous aromatic bouquet reveals notes of
                                    red berries, raspberries, blackberries, blueberries,
                                    plums. This wine will be an excellent accompaniment
                                    to meat dishes, as well as for solo consumption""",
                            IMAGE_PATH + "malbec-Red-Dry.png",
                            IMAGE_PATH + "malbec-Red-Dry3.png"
                    ));
            add(
                    new WineCreateRequestDto(
                            "MRD2022",
                            "Select",
                            null,
                            "Prince Trubetskoi Select Sauvignon Blanc",
                            "Sauvignon Blanc",
                            new BigDecimal("990"),
                            "Sauvignon Blanc",
                            false,
                            WineType.DRY,
                            new BigDecimal("9.5"),
                            new BigDecimal("14"),
                            WineColor.RED,
                            "rich, fresh, with aroma of green apples, blackcurrant "
                                    + "leaves and notes of meadow grass",
                            "rich, harmoniously combined with pleasant acidity",
                            "Rich, fresh, with aromas of green apples, black currant "
                                    + "leaves and notes of meadow grass",
                            "Pairs well with salads, seafood, fish and white poultry dishes",
                            """
                                    The Sauvignon Blanc grapes are harvested and sorted by hand.
                                    Next comes cold maceration on the pulp for up to 6 hours.
                                    The fermentation process takes place in stainless steel
                                    tanks. Next, the wine is aged on fine yeast lees for two
                                    months, after which the wine rests in the bottle for
                                    another month before going on sale. Sauvignon Blanc wine
                                    has a fresh aromatic character. This wine will be an
                                    excellent choice for a hot summer day and get-togethers
                                    with friends.""",
                            IMAGE_PATH + "Sauvignon-White-Dry.png",
                            IMAGE_PATH + "Sauvignon-White-Dry3.png"
                    ));
            add(
                    new WineCreateRequestDto(
                            "MRD2023",
                            "Select",
                            null,
                            "Prince Trubetskoi Select Shiraz",
                            "Shiraz",
                            new BigDecimal("850"),
                            "Shiraz",
                            false,
                            WineType.DRY,
                            new BigDecimal("13"),
                            new BigDecimal("15"),
                            WineColor.WHITE,
                            "dark red with purple highlights",
                            "juicy, full-bodied wine with a pleasant, long aftertaste, "
                                    + "with soft velvety tannins",
                            "rich, with notes of ripe plum and blackberry, as well as "
                                    + "hints of smoke, spice, animalic tones and green nuances",
                            "goes well with baked lamb, grilled steak, oriental "
                                    + "vegetables, vegetable salads and fish dishes",
                            """
                                    Plantings of shiraz grapes are located on the slope of
                                    the Kakhovka reservoir within the Black Sea depression
                                    of the East European Plain. The grapes are collected
                                    and sorted by hand. Cold infusion on the pulp for about
                                    6 hours. The main fermentation is in stainless steel
                                    containers, aging on fine yeast lees for 2 months and
                                    resting for at least 1 month in the bottle. This creates
                                    a deep, rich wine with a vibrant berry profile as well
                                    as rich spice and animalic tones. This wine goes well
                                    with meat dishes and grilled dishes""",
                            IMAGE_PATH + "Shiraz-RedDry.png",
                            IMAGE_PATH + "Shiraz-RedDry3.png"
                    ));
            add(
                    new WineCreateRequestDto(
                            "MRD2024",
                            "Select",
                            null,
                            "Prince Trubetskoi Select Pinot Blanc",
                            "Pinot Blanc",
                            new BigDecimal("690"),
                            "Pinot Blanc",
                            false,
                            WineType.DRY,
                            new BigDecimal("10.60"),
                            new BigDecimal("12.90"),
                            WineColor.RED,
                            "light straw",
                            "мягкий, деликатный, гармоничный с освежающей кислотностью",
                            "generous, varietal with notes of fresh fruit, peach "
                                    + "and a hint of citrus",
                            "goes well with vegetable salads and fish dishes",
                            """
                                    Pinot Blanc berries are harvested and sorted entirely by
                                    hand. The grapes are quickly delivered to the plant,
                                    which allows for minimal contact of the berries with
                                    oxygen. Cold maceration lasts up to six hours. The
                                    fermentation process takes place in steel tanks.
                                    At the end of fermentation, the wine is aged on fine
                                    yeast lees for two months. Before going on sale,
                                    the wine rests for a month in the bottle. Pinot Blanc
                                    is an incredibly fresh and aromatic wine. A beautiful
                                    bouquet reveals notes of fruit, citrus, and peach.
                                    This is a universal wine, both for a large company
                                    and for solo consumption""",
                            IMAGE_PATH + "Pinot-Blanc-White-Dry.png",
                            IMAGE_PATH + "Pinot-Blanc-White-Dry3.png"
                    ));
            add(
                    new WineCreateRequestDto(
                            "MRD2025",
                            "Grand Reserve",
                            null,
                            "Prince Trubetskoi Grand Reserve Oksamyt Ukrainy",
                            "Oksamyt Ukrainy",
                            new BigDecimal("1350"),
                            "Cabernet Sauvignon",
                            false,
                            WineType.DRY,
                            new BigDecimal("13"),
                            new BigDecimal("15"),
                            WineColor.WHITE,
                            "dark ruby with purple tint",
                            "medium-bodied, soft and enveloping, with velvety tannins and a "
                                    + "long fruity aftertaste",
                            "aroma of berries and fruits with notes of black currant, cherry "
                                    + "and ripe plum, delicate notes of black pepper",
                            "Pairs perfectly with veal with berry sauce and hard cheeses",
                            """
                                    This magnificent wine is made from 100% Cabernet Sauvignon.
                                    The berries are picked and sorted by hand. The wine is
                                    made with strict control of all processes and
                                    temperatures. After fermentation, the wine is aged. It
                                    is carried out in basements at a temperature of 10 to 15°C
                                    in oak containers for a period of at least 2 years.
                                    During aging, two open pourings and two closed ones
                                    are carried out. After aging, the wine is aged for an
                                    additional year in the bottle before going on sale.
                                    Oksamite Ukraine is a truly magnificent, refined
                                    and deep wine""",
                            IMAGE_PATH + "Velvet-of-Ukraine-Red-Dry.png",
                            IMAGE_PATH + "Velvet-of-Ukraine-Red-Dry3.png"
                    ));
            add(
                    new WineCreateRequestDto(
                            "MRD2026",
                            "Reserve",
                            null,
                            "Prince Trubetskoi Reserve Chardonnay",
                            "Chardonnay",
                            new BigDecimal("1020"),
                            "Chardonnay",
                            false,
                            WineType.DRY,
                            new BigDecimal("9.5"),
                            new BigDecimal("14"),
                            WineColor.RED,
                            "golden straw",
                            "soft enveloping wine with a pleasant note of grapefruit",
                            "light, multifaceted, with notes of flowers, ripe "
                                    + "fruits with hints of vanilla",
                            "goes well with Caprese salad, seafood sauté, "
                                    + "grilled chicken, Camembert cheese",
                            """
                                    Hand-picked grapes are sorted and destemmed. Grape juice
                                    is fermented at temperatures up to 15C in vertical
                                    thermally insulated containers using a pure culture
                                    of yeast from leading European producers specifically for
                                    the Chardonnay grape variety. After fermentation, the wine
                                    is removed from the yeast sediment and sent for aging.
                                    Aging is carried out in basements at a temperature of
                                    10 to 15 ° C in new French oak barrels for at least six
                                    months. After the aging period ends, the wine is sent for
                                    cold bottling. Chardonnay has a rich character and
                                    richness of flavor. This wine can make an incredible
                                    impression on you.""",
                            IMAGE_PATH + "Chardonnay-Reserve-White-Dry.png",
                            IMAGE_PATH + "Chardonnay-Reserve-White-Dry3.png"
                    ));
            add(
                    new WineCreateRequestDto(
                            "MRD2027",
                            "Reserve",
                            null,
                            "Prince Trubetskoi Reserve Merlot",
                            "Merlot",
                            new BigDecimal("910"),
                            "Merlot",
                            false,
                            WineType.DRY,
                            new BigDecimal("9.5"),
                            new BigDecimal("14"),
                            WineColor.WHITE,
                            "juicy, round, with notes of cherry and plum, "
                                    + "as well as hints of chocolate and spice",
                            "round, deep, with soft tannins",
                            "Juicy, round, with notes of cherry and plum, as well as "
                                    + "hints of chocolate and spice",
                            "goes well with grilled vegetables, fried quail",
                            """
                                    A generous and deep wine that is made from 100% merlot.
                                    The harvest is harvested by hand and the berries are
                                    sorted. During production, processes and temperatures
                                    are strictly controlled at all stages. After
                                    fermentation, the wine is sent for aging, which
                                    takes place in cellars at a temperature of 10 -
                                    15°C. The wine is aged for two years in new French
                                    oak barrels, which gives it a special character. Also
                                    during this process, two open and two closed
                                    transfers are carried out. Next, the wine is bottled
                                    and kept in the bottle for another year. Merlot wine
                                    has a rich and deep character. It will appeal to
                                    connoisseurs of generous and juicy wines""",
                            IMAGE_PATH + "Merlot-Red-Dry.png",
                            IMAGE_PATH + "Merlot-Red-Dry3.png"
                    ));
            add(
                    new WineCreateRequestDto(
                            "MRD2028",
                            "Select",
                            null,
                            "Prince Trubetskoi Select Aligote",
                            "Aligote",
                            new BigDecimal("650"),
                            "Aligote",
                            false,
                            WineType.DRY,
                            new BigDecimal("10.60"),
                            new BigDecimal("12.90"),
                            WineColor.WHITE,
                            "light, fresh, with beautiful shades of meadow herbs and flowers",
                            "light, fresh, balanced, with notes of flowers and fruits",
                            "light, fresh, with beautiful shades of meadow herbs and flowers",
                            "goes well with light fish and seafood appetizers",
                            """
                                    The wine was produced at an ancient Ukrainian chateau with a
                                    century-old history, the beginning of which is marked by
                                    the planting of the first grape vines for Prince Trubetskoy
                                    in 1896 on the Kozatsky estate in the Kherson province.
                                    Nowadays, as throughout history, the Trubetskoy winery
                                    uses grapes exclusively from its own vineyards to make
                                    wine, guaranteeing quality at all stages of production.
                                    Modern equipment, a creative approach, a combination of
                                    modern technologies and many years of experience - all
                                    this creates rich wines of the highest quality. You will
                                    like the white wine Aligote for its lightness and pleasant
                                    bouquet with hints of meadow herbs and flowers""",
                            IMAGE_PATH + "Aligote-White-Dry.png",
                            IMAGE_PATH + "Aligote-White-Dry3.png"
                    ));
            add(
                    new WineCreateRequestDto(
                            "MRD2029",
                            "Reserve",
                            null,
                            "Prince Trubetskoi Reserve CHATEAU TRUBETSKOI",
                            "CHATEAU TRUBETSKOI",
                            new BigDecimal("1050"),
                            "Cabernet Sauvignon, Cabernet Franc, Merlot, Petit Verdot",
                            false,
                            WineType.DRY,
                            new BigDecimal("9.5"),
                            new BigDecimal("14"),
                            WineColor.RED,
                            "bright, expressive, deep with notes of red and "
                                    + "black berries, plums and oak notes",
                            "full, harmonious, round with soft tannins",
                            "bright, expressive, deep with notes of red and black "
                                    + "berries, plums and oak notes",
                            "goes well with meat dishes and aged cheeses",
                            """
                                    It magnificent deep red wine was created at an
                                    ancient chateau in Ukraine, which has a centuries-old
                                    history of winemaking, the beginning of which is marked
                                    by the planting of the first grape vines for Prince
                                    Trubetskoy in 1896This red wine has become one of the
                                    most magnificent wines of the estate. A complex, deep,
                                    multifaceted aroma, in which shades of red and black
                                    berries play, as well as a long-lasting taste with
                                    velvety tannins and an expressive structure will not
                                    leave you indifferent. This wine will be an amazing
                                    complement to meat dishes and aged cheeses""",
                            IMAGE_PATH + "Chateau-Trubetskoi-Red-Dry.png",
                            IMAGE_PATH + "Chateau-Trubetskoi-Red-Dry3.png"
                    ));
            add(
                    new WineCreateRequestDto(
                            "MRD2030",
                            "Select",
                            null,
                            "Prince Trubetskoi Select Chardonnay",
                            "Chardonnay",
                            new BigDecimal("1020"),
                            "Chardonnay",
                            false,
                            WineType.DRY,
                            new BigDecimal("10.60"),
                            new BigDecimal("12.90"),
                            WineColor.WHITE,
                            "straw yellow",
                            "clean, soft, with delicate refreshing acidity and "
                                    + "a pleasant aftertaste",
                            "fruity, bright, revealing notes of peach, "
                                    + "apple and vanilla",
                            "Serve with green salads, Italian bruschetta, "
                                    + "fried chicken, seafood sauté, brie cheese",
                            """
                                    Prince Trubetskoy Select Chardonnay is a harmonious wine
                                    with excellent structure, created from the French
                                    Chardonnay variety, loved all over the world. The
                                    vines are cultivated on the slopes of the Kakhovka
                                    reservoir with ideal exposure and soils (medium loamy
                                    chernozems). Fermentation and maturation of the wine
                                    takes place in neutral containers with full temperature
                                    control. This wine is ideal for lunch or dinner with
                                    family or friends""",
                            IMAGE_PATH + "Chardonnay-White-Dry.png",
                            IMAGE_PATH + "Chardonnay-White-Dry3.png"
                    ));
            add(
                    new WineCreateRequestDto(
                            "MRD2031",
                            "Grand Reserve",
                            null,
                            "Prince Trubetskoi Grand Reserve Perlyna Stepu",
                            "Perlyna Stepu",
                            new BigDecimal("950"),
                            "Aligote",
                            false,
                            WineType.DRY,
                            new BigDecimal("10.60"),
                            new BigDecimal("12.90"),
                            WineColor.WHITE,
                            "dark golden",
                            "full-bodied, rich, slightly oily, well balanced, with a long finish",
                            "filled with notes of wildflowers and herbs, ripe pear, "
                                    + "vanilla and spices",
                            """
                                    An excellent aperitif, goes well with salmon carpaccio with
                                    capers, pasta with seafood, risotto with rapana, cheese
                                    plateau with nuts""",
                            """
                                    The winery of Prince Trubetskoy was created more than
                                    100 years ago. Here are unique climatic conditions,
                                    soil composition, correct exposure, and most importantly,
                                    people who love their lands and their product. A striking
                                    representative of the winery's Grand Reserve collection
                                    is the Perlina Stepu wine, created on the basis of the
                                    Aligote variety. A special feature of this white wine
                                    is its long aging in the estate’s cellars with periodic
                                    decanting of the wine. The wine is aged for 1.5 years
                                    in new French barrels. Perlina Stepu is an ideal gift
                                    for Aligote lovers""",
                            IMAGE_PATH + "Steppe-Pearl-White-Dry.png",
                            IMAGE_PATH + "Steppe-Pearl-White-Dry3.png"
                    ));
            add(
                    new WineCreateRequestDto(
                            "MRD2032",
                            "Reserve",
                            null,
                            "Prince Trubetskoi Reserve Pinot Noir",
                            "Pinot Noir",
                            new BigDecimal("750"),
                            "Pinot Noir",
                            false,
                            WineType.DRY,
                            new BigDecimal("13"),
                            new BigDecimal("15"),
                            WineColor.RED,
                            """
                                    refined, harmonious, with tones of black
                                    and red berries, fruits, vanilla,
                                    coffee and chocolate """,
                            "aristocratic, balanced, filled with notes of plum, raspberry, "
                                    + "cinnamon, mocha, with an excellent tannin structure and "
                                    + "a long finish",
                            """
                                    sophisticated, harmonious, with tones of black and
                                    red berries, fruits, vanilla, coffee and chocolate""",
                            "wonderful combination with duck, beef, grilled "
                                    + "tuna and Emental cheese",
                            """
                                    Winery of Prince P.N. Trubetskoy produces excellent
                                    Ukrainian wines, thanks to the wonderful terroir,
                                    modern equipment and technologies, as well as a
                                    team of wine specialists who are passionate about
                                    their work, following global trends in wine production.
                                    Prince Trubetskoy Reserve Pinot Noir is an aristocratic
                                    and elegant wine. Pinot Noir grapes are harvested by hand,
                                    sorted and destemmed. Fermentation takes place under
                                    controlled temperatures in neutral tanks. The wine is aged
                                    in the winery cellars in oak barrels for a minimum
                                    of 9 months""",
                            IMAGE_PATH + "Pinot-Noir-Red-Dry.png",
                            IMAGE_PATH + "Pinot-Noir-Red-Dry3.png"
                    ));
        }
    }
}
