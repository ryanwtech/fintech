package com.fintech.repo;

import com.fintech.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByUserIdAndIsActiveTrue(UUID userId);
    
    List<Category> findByUserId(UUID userId);
    
    @Query("SELECT c FROM Category c WHERE c.userId = :userId AND c.id = :categoryId")
    Category findByUserIdAndId(@Param("userId") UUID userId, @Param("categoryId") UUID categoryId);
    
    boolean existsByUserIdAndId(UUID userId, UUID categoryId);
    
    @Query("SELECT c FROM Category c WHERE c.userId IS NULL AND c.isActive = true")
    List<Category> findGlobalCategories();
    
    @Query("SELECT COUNT(t) > 0 FROM Transaction t WHERE t.categoryId = :categoryId")
    boolean existsTransactionsByCategoryId(@Param("categoryId") UUID categoryId);
}
