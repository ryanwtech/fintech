package com.fintech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 1, max = 100, message = "Category name must be between 1 and 100 characters")
    private String name;

    private String description;

    @Size(max = 7, message = "Color must be a valid hex color (max 7 characters)")
    private String color;

    @Size(max = 50, message = "Icon name must be max 50 characters")
    private String icon;

    @NotNull(message = "Income type is required")
    private Boolean isIncome;
}
