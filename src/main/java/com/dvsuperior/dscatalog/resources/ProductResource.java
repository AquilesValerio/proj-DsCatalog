package com.dvsuperior.dscatalog.resources;

import com.dvsuperior.dscatalog.DTO.CategoryDTO;
import com.dvsuperior.dscatalog.DTO.ProductDTO;
import com.dvsuperior.dscatalog.services.CategoryService;
import com.dvsuperior.dscatalog.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {

    private final ProductService service;


    public ProductResource(ProductService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAll(Pageable pageable ) {

        // PARAMETROS: page, size, sort

        Page<ProductDTO> dtoPage = service.findAllPaged(pageable);
        return ResponseEntity.ok().body(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        ProductDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> insertProduct(@RequestBody ProductDTO dto) {
        dto = service.insertProduct(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        dto = service.updateProduct(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
