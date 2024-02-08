package com.winestoreapp.controller;

import com.winestoreapp.dto.wine.WineCreateRequestDto;
import com.winestoreapp.dto.wine.WineDto;
import com.winestoreapp.service.WineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final WineService wineService;

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
        return wineService.getPictureByIdByPath(id);
    }

    @Operation(summary = "Find wine by id",
            description = "Find existing wine by id")
    @GetMapping("/{id}")
    public WineDto findWineById(
            @PathVariable Long id) {
        return wineService.findById(id);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Delete wine by id",
            description = "Delete existing wine by id. Available for manager12345@gmail.com")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean deleteWineById(
            @PathVariable Long id) {
        return wineService.isDeleteById(id);
    }

    @Operation(summary = "Find all wines",
            description = """
                    Find all wines. You can set pagination by: page, size, and sort parameters. 
                    By default, size = 4, page = 0, sort by 'averageRatingScore,DESC' 
                    and after sort by 'id,ASC'""")
    @GetMapping
    public List<WineDto> findAllWines(
            @PageableDefault(
                    size = 4,
                    page = 0,
                    sort = {"averageRatingScore", "id"},
                    direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return wineService.findAll(pageable);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Creating a new Wine.",
            description =
                    "Creating a new Wine with valid data. Available for manager12345@gmail.com")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WineDto createWine(
            @RequestBody @Valid WineCreateRequestDto createDto) {
        return wineService.add(createDto);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Add an image into db.",
            description = "Add an image into database. Available for manager12345@gmail.com")
    @PatchMapping("/{id}/image/db")
    @ResponseStatus(HttpStatus.OK)
    public URL addImageByIdIntoDb(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws MalformedURLException {
        return wineService.updateImage(id, file, true);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Add an image into path.",
            description = "Add an image into path. Available for manager12345@gmail.com")
    @PatchMapping("/{id}/image/path")
    @ResponseStatus(HttpStatus.OK)
    public URL addImageByIdIntoPath(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws MalformedURLException {
        return wineService.updateImage(id, file, false);
    }
}
