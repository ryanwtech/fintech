package com.fintech.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "categories", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "name"})
})
@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 7)
    private String color; // Hex color code

    @Column(length = 50)
    private String icon;

    @Column(name = "is_income")
    private Boolean isIncome = false;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
