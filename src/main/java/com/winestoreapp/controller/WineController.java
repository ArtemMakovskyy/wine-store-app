package com.winestoreapp.controller;

import com.winestoreapp.dto.wine.WineCreateRequestDto;
import com.winestoreapp.dto.wine.WineDto;
import com.winestoreapp.service.WineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.MalformedURLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Wine management", description = "Endpoints to managing wines")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST,
        RequestMethod.PATCH, RequestMethod.DELETE})
@RequestMapping("/wines")
public class WineController {
    private final WineService wineService;

    @Operation(summary = "Find wine by id",
            description = "Find existing wine by id. Available for all users.")
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
                    By default, size 50, page = 0, sort by 'averageRatingScore,DESC' 
                    and after sort by 'id,DESC'. 
                    Pagination example: /wines?size=5&page=0&sort=id
                    Available for all users.""")
    @GetMapping
    public List<WineDto> findAllWines(
            @PageableDefault(
                    page = 0,
                    size = 50,
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
    @Operation(summary = "Add an image into path.",
            description = """
    Add an image into path. Available for manager12345@gmail.com.
    After you add or change photos, you will have to restart the app to view the changes.""")
    @PatchMapping("/{id}/image")
    @ResponseStatus(HttpStatus.OK)
    public WineDto addImageByIdIntoPath(
            @PathVariable Long id,
            @RequestParam("imageA") MultipartFile imageA,
            @RequestParam("imageB") MultipartFile imageB
    ) throws MalformedURLException {
        return wineService.updateImage(id, imageA, imageB);
    }
}
