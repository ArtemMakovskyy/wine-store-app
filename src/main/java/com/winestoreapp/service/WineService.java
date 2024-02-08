package com.winestoreapp.service;

import com.winestoreapp.dto.wine.WineCreateRequestDto;
import com.winestoreapp.dto.wine.WineDto;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface WineService {

    WineDto add(WineCreateRequestDto createDto);

    List<WineDto> findAll(Pageable pageable);

    WineDto findById(Long id);

    boolean isDeleteById(Long id);

    URL updateImage(Long id, MultipartFile image, boolean addToDb) throws MalformedURLException;

    ResponseEntity<Resource> getPictureByIdFromDb(Long id) throws IOException;

    ResponseEntity<Resource> getPictureByIdByPath(Long id) throws IOException;
}
