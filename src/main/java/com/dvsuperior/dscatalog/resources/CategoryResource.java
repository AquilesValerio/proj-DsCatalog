package com.dvsuperior.dscatalog.resources;

import com.dvsuperior.dscatalog.DTO.CategoryDTO;
import com.dvsuperior.dscatalog.services.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {

    private final CategoryService service;


    public CategoryResource(CategoryService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> findAll(Pageable pageable) {

        Page<CategoryDTO> dtoPage = service.findAllPaged(pageable);
        return ResponseEntity.ok().body(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
        CategoryDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> insertCategory(@RequestBody CategoryDTO dto) {
        dto = service.insertCategory(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        dto = service.updateCategory(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
