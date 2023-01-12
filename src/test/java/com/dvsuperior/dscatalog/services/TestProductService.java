package com.dvsuperior.dscatalog.services;

import com.dvsuperior.dscatalog.DTO.ProductDTO;
import com.dvsuperior.dscatalog.entities.Category;
import com.dvsuperior.dscatalog.entities.Product;
import com.dvsuperior.dscatalog.factories.Factory;
import com.dvsuperior.dscatalog.repositories.CategoryRepository;
import com.dvsuperior.dscatalog.repositories.ProductRepository;
import com.dvsuperior.dscatalog.services.exceptions.DatabaseException;
import com.dvsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class TestProductService {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private Long existingId;
    private Long noExistingId;
    private Long dependentId;
    private PageImpl<Product> page;
    private Product product;

    private ProductDTO productDTO;

    private Category category;
   // private Category categoryDTO;


    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        noExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        category = Factory.createCategory();
        productDTO = Factory.createProductDTO();
        //productDTO =  new ProductDTO(10L, "TESTE", "Good Phone", 800.0, "https://img.com/img.png", Instant.parse("2020-10-20T03:00:00Z"));
        page = new PageImpl<>(List.of(product));

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.findById(noExistingId)).thenReturn(Optional.empty());
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));

        Mockito.when(repository.getReferenceById(product.getId())).thenReturn(product);
        Mockito.when(repository.getReferenceById(noExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(noExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(noExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

    }

    @Test
    void updateProductShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists(){

        Assertions.assertThrows(ResourceNotFoundException.class, ()->{
            service.updateProduct(noExistingId, productDTO);
        });

    }

    @Test
    void updateProductShouldReturnProductDtoWhenIdExists(){

        ProductDTO teste = new ProductDTO(100L, "TESTE", "Good Phone",
                800.0, "https://img.com/img.png", Instant.parse("2020-10-20T03:00:00Z"));

        ProductDTO result;

        result = service.updateProduct(product.getId(),teste);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("TESTE", result.getName());

    }

    @Test
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){

        Assertions.assertThrows(ResourceNotFoundException.class,()->{
            service.findById(noExistingId);
        });

    }

    @Test
    void findByIdShouldReturnProductDtoWhenIdExist(){

        ProductDTO result = service.findById(existingId);

         Assertions.assertNotNull(result);
         Assertions.assertEquals(existingId, result.getId());
         Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    void findAllPagedShouldReturnPage(){
        Pageable pageable = PageRequest.of(0,10);

        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }


   @Test
   void deleteByIdShouldThrowDatabaseExceptionWhenDependentId(){
        Assertions.assertThrows(DatabaseException.class, ()->{
            service.deleteProduct(dependentId);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
   }

    @Test
    void deleteByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteProduct(noExistingId);
        });
        Mockito.verify(repository, Mockito.times(1)).deleteById(noExistingId);
    }

    @Test
    void deleteByIdShouldDoNothingWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> {
            service.deleteProduct(existingId);
        });
    }
}


