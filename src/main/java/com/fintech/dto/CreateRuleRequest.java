package com.fintech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CreateRuleRequest {

    @NotBlank(message = "Rule name is required")
    @Size(min = 1, max = 100, message = "Rule name must be between 1 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be 500 characters or less")
    private String description;

    @NotBlank(message = "Conditions are required")
    private String conditions;

    @NotBlank(message = "Actions are required")
    private String actions;

    private Integer priority = 0;

    private Boolean enabled = true;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
