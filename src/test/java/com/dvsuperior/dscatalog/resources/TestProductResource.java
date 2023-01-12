package com.dvsuperior.dscatalog.resources;

import com.dvsuperior.dscatalog.DTO.ProductDTO;
import com.dvsuperior.dscatalog.factories.Factory;
import com.dvsuperior.dscatalog.services.ProductService;
import com.dvsuperior.dscatalog.services.exceptions.DatabaseException;
import com.dvsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)
public class TestProductResource {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    private PageImpl<ProductDTO> page;
    private ProductDTO productDTO;
    private Long existingId;
    private Long noExistingId;
    private Long dependentId;

    @BeforeEach
    void setUp() throws Exception {
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));
        existingId = 1L;
        noExistingId = 1000L;
        dependentId = 3L;

        when(service.findAllPaged(any())).thenReturn(page);

        when(service.findById(existingId)).thenReturn(productDTO);
        when(service.findById(noExistingId)).thenThrow(ResourceNotFoundException.class);

        when(service.updateProduct(eq(existingId), any())).thenReturn(productDTO);
        when(service.updateProduct(eq(noExistingId), any())).thenThrow(ResourceNotFoundException.class);

        when(service.insertProduct(any())).thenReturn(productDTO);

        Mockito.doNothing().when(service).deleteProduct(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(service).deleteProduct(noExistingId);
        Mockito.doThrow(DatabaseException.class).when(service).deleteProduct(dependentId);
    }


    @Test
    void insertProductShouldReturnProductDto() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(post("/products")
                                .content(jsonBody)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated());

        result.andExpect(jsonPath("$.name").isString());
        result.andExpect(jsonPath("$.name").isNotEmpty());
    }

    @Test
    void deleteProductDtoThrowsDataBaseExceptionWhenIdDoesIsDependent() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/products/{id}", dependentId))
                        .andExpect(status().is5xxServerError());
    }

    @Test
    void deleteProductDtoThrowsResourceNotFoundExceptionWhenIdDoesNotExists() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/products/{id}", noExistingId))
                        .andExpect(status().isNotFound());
    }

    @Test
    void deleteProductDtoWhenIdExists() throws Exception {

        ResultActions result =
                mockMvc.perform(delete("/products/{id}", existingId))
                        .andExpect(status().isNoContent());
    }

    @Test
    void updateProductShouldReturnProductDTOWhenIdExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result =

                mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.name").isString());
        result.andExpect(jsonPath("$.price").isNumber());

    }

    @Test
    void updateProductShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result =
                mockMvc.perform(put("/products/{id}", noExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturnProductWhenIdExists() throws Exception {
        mockMvc.perform(get("/products/{id}", existingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.price").isNumber());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        mockMvc.perform(get("/products/{id}", noExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(get("/products")).andExpect(status().isOk());
    }

    /*
    ------------------------Outra forma de fazer as assertions----------------------------------
    @Test
    void findAllShouldReturnPage() throws Exception{
    ResultActions result
        mockMvc.perform(get("/products"))
        .accept(MediaType.Application_JSON);

        result.andExpect(Status().isOk());
    }*/


}
