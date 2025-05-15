package com.fintech.dto;

import com.fintech.domain.Category;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CategoryDto {
    private UUID id;
    private String name;
    private String description;
    private String color;
    private String icon;
    private Boolean isIncome;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CategoryDto fromEntity(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setColor(category.getColor());
        dto.setIcon(category.getIcon());
        dto.setIsIncome(category.getIsIncome());
        dto.setIsActive(category.getIsActive());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
}
