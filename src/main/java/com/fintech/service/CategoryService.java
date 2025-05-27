package com.fintech.service;

import com.fintech.domain.Category;
import com.fintech.dto.CategoryDto;
import com.fintech.dto.CreateCategoryRequest;
import com.fintech.dto.UpdateCategoryRequest;
import com.fintech.repo.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuditLogService auditLogService;

    public List<CategoryDto> getUserCategories(UUID userId) {
        List<Category> categories = categoryRepository.findByUserIdAndIsActiveTrue(userId);
        List<Category> globalCategories = categoryRepository.findGlobalCategories();
        
        // Combine user categories and global categories
        categories.addAll(globalCategories);
        
        return categories.stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(UUID userId, UUID categoryId) {
        Category category = categoryRepository.findByUserIdAndId(userId, categoryId);
        if (category == null) {
            // Check global categories
            category = categoryRepository.findById(categoryId).orElse(null);
            if (category == null || category.getUserId() != null) {
                throw new RuntimeException("Category not found");
            }
        }
        return CategoryDto.fromEntity(category);
    }

    public CategoryDto createCategory(UUID userId, CreateCategoryRequest request) {
        // Check if category name already exists for this user
        List<Category> existingCategories = categoryRepository.findByUserId(userId);
        boolean nameExists = existingCategories.stream()
                .anyMatch(cat -> cat.getName().equalsIgnoreCase(request.getName()) && cat.getIsActive());
        
        if (nameExists) {
            throw new RuntimeException("Category with this name already exists");
        }

        Category category = new Category();
        category.setUserId(userId);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setColor(request.getColor());
        category.setIcon(request.getIcon());
        category.setIsIncome(request.getIsIncome());
        category.setIsActive(true);

        Category savedCategory = categoryRepository.save(category);
        
        // Log audit
        auditLogService.logCategoryAction(com.fintech.domain.AuditLog.AuditAction.CREATE, savedCategory, null);
        
        return CategoryDto.fromEntity(savedCategory);
    }

    public CategoryDto updateCategory(UUID userId, UUID categoryId, UpdateCategoryRequest request) {
        Category category = categoryRepository.findByUserIdAndId(userId, categoryId);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }

        // Check if new name conflicts with existing categories
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            List<Category> existingCategories = categoryRepository.findByUserId(userId);
            boolean nameExists = existingCategories.stream()
                    .anyMatch(cat -> cat.getName().equalsIgnoreCase(request.getName()) 
                            && cat.getIsActive() && !cat.getId().equals(categoryId));
            
            if (nameExists) {
                throw new RuntimeException("Category with this name already exists");
            }
        }

        // Update fields if provided
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getColor() != null) {
            category.setColor(request.getColor());
        }
        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }
        if (request.getIsIncome() != null) {
            category.setIsIncome(request.getIsIncome());
        }

        // Store old values for audit
        Category oldCategory = createCategoryCopy(category);

        Category savedCategory = categoryRepository.save(category);
        
        // Log audit
        auditLogService.logCategoryAction(com.fintech.domain.AuditLog.AuditAction.UPDATE, savedCategory, oldCategory);
        
        return CategoryDto.fromEntity(savedCategory);
    }

    public void deleteCategory(UUID userId, UUID categoryId) {
        Category category = categoryRepository.findByUserIdAndId(userId, categoryId);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }

        // Check if category is in use by transactions
        if (categoryRepository.existsTransactionsByCategoryId(categoryId)) {
            throw new RuntimeException("Cannot delete category that is in use by transactions");
        }

        // Soft delete
        category.setIsActive(false);
        
        // Log audit before soft deletion
        auditLogService.logCategoryAction(com.fintech.domain.AuditLog.AuditAction.DELETE, category, null);
        
        categoryRepository.save(category);
    }

    private Category createCategoryCopy(Category original) {
        Category copy = new Category();
        copy.setId(original.getId());
        copy.setUserId(original.getUserId());
        copy.setName(original.getName());
        copy.setDescription(original.getDescription());
        copy.setColor(original.getColor());
        copy.setIcon(original.getIcon());
        copy.setIsIncome(original.getIsIncome());
        copy.setIsActive(original.getIsActive());
        copy.setCreatedAt(original.getCreatedAt());
        copy.setUpdatedAt(original.getUpdatedAt());
        return copy;
    }
}
