package com.fintech.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "rules")
@Data
@EqualsAndHashCode(callSuper = true)
public class Rule extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "jsonb", nullable = false)
    private String conditions; // Rule conditions as JSON

    @Column(columnDefinition = "jsonb", nullable = false)
    private String actions; // Rule actions as JSON

    @Column
    private Integer priority = 0;

    @Column
    private Boolean enabled = true;
}
