package com.winestoreapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WineControllerTest {
    protected static MockMvc mockMvc;
    private static final Long WINE_CAR_ID = 1L;
    private static final int WINE_CAR_SIZE = 14;
    private static final Integer WINE_YEAR = 2019;
    private static final String WINE_GRAPE = "Riesling";
    private static final String WINE_VENDOR_CODE = "MRD2019";

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Get existent wine by valid ID")
    void findWineById_ValidCarId_ReturnWineDto() throws Exception {
        mockMvc.perform(
                        get("/wines/{id}", WINE_CAR_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(WINE_CAR_ID))
                .andExpect(jsonPath("$.year").value(WINE_YEAR))
                .andExpect(jsonPath("$.grape").value(WINE_GRAPE))
                .andExpect(jsonPath("$.vendorCode").value(WINE_VENDOR_CODE));
    }

    @Test
    @DisplayName("Find all existent wines")
    void findAllWines_WithValidData_Success() throws Exception {
        mockMvc.perform(get("/wines")
                        .param("page", "0")
                        .param("size", "50")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(WINE_CAR_SIZE)));
    }
}
