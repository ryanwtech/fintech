package com.fintech.web;

import com.fintech.dto.CategoryDto;
import com.fintech.dto.CreateCategoryRequest;
import com.fintech.dto.UpdateCategoryRequest;
import com.fintech.service.AuthService;
import com.fintech.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getUserCategories(Authentication authentication) {
        try {
            String email = authentication.getName();
            UUID userId = authService.getUserByEmail(email).getId();
            List<CategoryDto> categories = categoryService.getUserCategories(userId);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable UUID id, Authentication authentication) {
        try {
            String email = authentication.getName();
            UUID userId = authService.getUserByEmail(email).getId();
            CategoryDto category = categoryService.getCategoryById(userId, id);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest request, 
                                                    Authentication authentication) {
        try {
            String email = authentication.getName();
            UUID userId = authService.getUserByEmail(email).getId();
            CategoryDto category = categoryService.createCategory(userId, request);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable UUID id, 
                                                    @Valid @RequestBody UpdateCategoryRequest request,
                                                    Authentication authentication) {
        try {
            String email = authentication.getName();
            UUID userId = authService.getUserByEmail(email).getId();
            CategoryDto category = categoryService.updateCategory(userId, id, request);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id, Authentication authentication) {
        try {
            String email = authentication.getName();
            UUID userId = authService.getUserByEmail(email).getId();
            categoryService.deleteCategory(userId, id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
