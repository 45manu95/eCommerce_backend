package com.manuel.ecommerce.controllers;

import com.manuel.ecommerce.entities.Category;
import com.manuel.ecommerce.services.CategoryService;
import com.manuel.ecommerce.support.exceptions.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> addCategory(@Valid @RequestBody Category category) {
        try{
            Category added = categoryService.addCategory(category);
            return ResponseEntity.status(HttpStatus.OK).body(added);
        } catch (CategoryAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Categoria già presente");
        }
    }

    @DeleteMapping("/remove/{name}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<String> removeCategory(@PathVariable String name){
        try{
            categoryService.removeCategory(name);
            return ResponseEntity.status(HttpStatus.OK).body("Categoria rimossa");
        }catch (CategoryNotExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Categoria inesistente");

        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('role_admin') or hasRole('role_user')")
    public ResponseEntity<Object> getAllCategories(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                  @RequestParam(value = "sortBy", defaultValue = "name") String sortBy){
        List<Category> result = categoryService.getAllCategories(pageNumber,pageSize,sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/search/{categoryName}")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<Object> searchCategory(@PathVariable String categoryName, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                  @RequestParam(value = "sortBy", defaultValue = "name") String sortBy){
        List<Category> result = categoryService.searchCategory(categoryName, pageNumber, pageSize, sortBy);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Nessun risultato!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
