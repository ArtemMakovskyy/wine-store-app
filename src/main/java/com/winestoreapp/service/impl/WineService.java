package com.winestoreapp.service.impl;

import com.winestoreapp.dto.wine.WineCreateRequestDto;
import com.winestoreapp.dto.wine.WineDto;
import java.util.List;
import java.util.Optional;

public interface WineService {
    WineDto add(WineCreateRequestDto createDto);
//
//    List<WineDto> findAll();
//
//    WineDto findById(Long id);
//
//    boolean isDeleteById(Long id);
}
