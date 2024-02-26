package com.winestoreapp.service;

import com.winestoreapp.dto.wine.WineCreateRequestDto;
import com.winestoreapp.dto.wine.WineDto;
import java.net.MalformedURLException;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface WineService {

    WineDto add(WineCreateRequestDto createDto);

    List<WineDto> findAll(Pageable pageable);

    WineDto findById(Long id);

    boolean isDeleteById(Long id);

    WineDto updateImage(Long id, MultipartFile imageA, MultipartFile imageB)
            throws MalformedURLException;
}
