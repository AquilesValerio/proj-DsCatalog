package com.dvsuperior.dscatalog.resources;

import com.dvsuperior.dscatalog.DTO.ProductDTO;
import com.dvsuperior.dscatalog.factories.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class integrationProductResource {

    @Autowired
    private MockMvc mockMvc;

    private Long existingId;
    private Long noExistingId;
    private Long countProducts;

    private ProductDTO productDTO;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        noExistingId = 100000L;
        countProducts = 25L;
        productDTO = Factory.createProductDTO();

    }

    @Test
    void findAllShouldReturnSortedPageWhenSortByName() throws Exception {

        ResultActions result =
                mockMvc.perform(get("/products/?page=0s&ize=12&sort=name,asc")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.content").isNotEmpty());
        result.andExpect(jsonPath("$.totalElements").value(countProducts));
        result.andExpect(jsonPath("content").exists());
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }

    @Test
    void updateProductShouldUpdateProductDtoWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        String expectedName = productDTO.getName();
        Long expectedId = productDTO.getId();
        ResultActions result =
                mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.name").value(expectedName));
        result.andExpect(jsonPath("$.id").value(expectedId));
    }

    @Test
    void updateProductShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

              ResultActions result =
                mockMvc.perform(put("/products/{id}", noExistingId)
                        .content(jsonBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

}
