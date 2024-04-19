package com.winestoreapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.winestoreapp.dto.mapper.WineMapper;
import com.winestoreapp.dto.wine.WineCreateRequestDto;
import com.winestoreapp.dto.wine.WineDto;
import com.winestoreapp.model.Wine;
import com.winestoreapp.model.WineColor;
import com.winestoreapp.model.WineType;
import com.winestoreapp.repository.WineRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class WineServiceImplTest {
    @Mock
    private WineMapper wineMapper;
    @Mock
    private WineRepository wineRepository;
    @InjectMocks
    private WineServiceImpl wineService;

    @Test
    @DisplayName("Add wine by valid wine and return WineDto")
    void add_validWine_ReturnWineDto() {
        //given
        WineCreateRequestDto wineCreateRequestDto = getWineCreateRequestDto();
        Wine wine = getWine();
        Mockito.when(wineMapper.toEntity(wineCreateRequestDto)).thenReturn(wine);
        Mockito.when(wineRepository.save(wine)).thenReturn(wine);
        Mockito.when(wineMapper.toDto(wine)).thenReturn(getWineDto());

        //when
        final WineDto actual = wineService.add(wineCreateRequestDto);

        //then
        assertEquals(getWineDto(), actual);
        Mockito.verify(wineMapper, Mockito.times(1)).toEntity(wineCreateRequestDto);
        Mockito.verify(wineRepository, Mockito.times(1)).save(wine);
        Mockito.verify(wineMapper, Mockito.times(1)).toDto(wine);
        verifyNoMoreInteractions(wineMapper, wineRepository);
    }

    @Test
    @DisplayName("Verify findAll method works")
    void findAll_ValidPageable_ShouldReturnAllWines() {
        //given
        final Pageable pageable = PageRequest.of(0, 10);
        final List<Wine> wines = List.of(getWine());
        final PageImpl<Wine> winePage = new PageImpl<>(wines, pageable, wines.size());
        final WineDto wineDto = getWineDto();

        Mockito.when(wineRepository.findAll(pageable)).thenReturn(winePage);
        Mockito.when(wineMapper.toDto(any())).thenReturn(wineDto);

        //when
        final List<WineDto> actual = wineService.findAll(pageable);

        //then
        assertEquals(wines.size(), 1);
        assertTrue(actual.contains(wineDto));
        verify(wineRepository, times(1)).findAll(pageable);
        verify(wineMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(wineRepository, wineMapper);
    }

    @Test
    @DisplayName("Find wine by valid ID then return WineDto")
    void findById_ValidWineId_ReturnWineDto() {
        //given
        Long wineId = anyLong();
        final Wine wine = getWine();
        final WineDto expected = getWineDto();

        Mockito.when(wineRepository.findById(wineId)).thenReturn(Optional.of(wine));
        Mockito.when(wineMapper.toDto(wine)).thenReturn(expected);

        //when
        final WineDto actual = wineService.findById(wineId);

        //then
        assertEquals(expected, actual);
        Mockito.verify(wineRepository, Mockito.times(1)).findById(wineId);
        Mockito.verify(wineMapper, Mockito.times(1)).toDto(wine);
        verifyNoMoreInteractions(wineRepository, wineMapper);
    }

    @Test
    @DisplayName("Delete wine by valid id. Should return true")
    void isDeleteById_ValidId_ShouldReturnTrue() {
        //given
        final long wineId = anyLong();

        Mockito.when(wineRepository.existsById(wineId)).thenReturn(true);

        //when
        final boolean actual = wineService.isDeleteById(wineId);

        //then
        assertTrue(actual);
        verify(wineRepository, times(1)).deleteById(wineId);
    }

    private WineDto getWineDto() {
        WineDto wineDto = new WineDto();
        wineDto.setId(1L);
        wineDto.setVendorCode("MRD2019");
        wineDto.setQualityLevel("Select");
        wineDto.setReserveType(null);
        wineDto.setName("Prince Trubetskoi Select Riesling");
        wineDto.setShortName("Riesling");
        wineDto.setYear(2019);
        wineDto.setTasteWine("asian food");
        wineDto.setPrice(new BigDecimal("870"));
        wineDto.setGrape("Riesling");
        wineDto.setIsDecantation(false);
        wineDto.setWineType(WineType.DRY);
        wineDto.setStrengthFrom(new BigDecimal("10.6"));
        wineDto.setStrengthTo(new BigDecimal("12.9"));
        wineDto.setWineColor(WineColor.WHITE);
        wineDto.setColorDescribing("citric");
        wineDto.setTaste("delicate, balanced, round, with a fruity and honey aftertaste.");
        wineDto.setAroma("""
                soft, generous, multifaceted, with hints of tropical fruits,
                notes of lychee and peach""");
        wineDto.setGastronomy("Recommended for oriental dishes and fruits.");
        wineDto.setDescription("""
                Vineyards stretch on the slopes of the Kakhovka reservoir.
                The unique terroir produces excellent wines. The harvest is
                harvested and sorted by hand. Fermentation of the wine, as
                well as maturation, takes place in tanks and is strictly
                controlled. Riesling is incredibly generous, multi-faceted
                and aromatic. Pleasant fruity and honey shades will give
                a truly vivid impression. Everyone likes this wine and
                is absolutely universal""");
        wineDto.setPictureLink("src/main/resources/static/images/wine/imageA.png");
        wineDto.setPictureLink2("src/main/resources/static/images/wine/imageB.png");
        return wineDto;
    }

    private Wine getWine() {
        Wine wine = new Wine();
        wine.setId(1L);
        wine.setVendorCode("MRD2019");
        wine.setQualityLevel("Select");
        wine.setReserveType(null);
        wine.setName("Prince Trubetskoi Select Riesling");
        wine.setShortName("Riesling");
        wine.setYear(2019);
        wine.setTasteWine("asian food");
        wine.setPrice(new BigDecimal("870"));
        wine.setGrape("Riesling");
        wine.setIsDecantation(false);
        wine.setWineType(WineType.DRY);
        wine.setStrengthFrom(new BigDecimal("10.6"));
        wine.setStrengthTo(new BigDecimal("12.9"));
        wine.setWineColor(WineColor.WHITE);
        wine.setColorDescribing("citric");
        wine.setTaste("delicate, balanced, round, with a fruity and honey aftertaste.");
        wine.setAroma("""
                soft, generous, multifaceted, with hints of tropical fruits,
                notes of lychee and peach""");
        wine.setGastronomy("Recommended for oriental dishes and fruits.");
        wine.setDescription("""
                Vineyards stretch on the slopes of the Kakhovka reservoir.
                The unique terroir produces excellent wines. The harvest is
                harvested and sorted by hand. Fermentation of the wine, as
                well as maturation, takes place in tanks and is strictly
                controlled. Riesling is incredibly generous, multi-faceted
                and aromatic. Pleasant fruity and honey shades will give
                a truly vivid impression. Everyone likes this wine and
                is absolutely universal""");
        wine.setPictureLink("src/main/resources/static/images/wine/imageA.png");
        wine.setPictureLink2("src/main/resources/static/images/wine/imageB.png");
        return wine;
    }

    private WineCreateRequestDto getWineCreateRequestDto() {
        return new WineCreateRequestDto(
                "MRD2019",
                "Select",
                null,
                "Prince Trubetskoi Select Riesling",
                "Riesling",
                2019,
                "asian food",
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
                "src/main/resources/static/images/wine/imageA.png",
                "src/main/resources/static/images/wine/imageB.png");
    }
}
