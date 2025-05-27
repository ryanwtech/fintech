package com.fintech.integration;

import com.fintech.domain.Category;
import com.fintech.repo.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    }

    @Test
    void shouldFindUserCategories() {
        // When
        List<Category> categories = categoryRepository.findByUserIdAndIsActiveTrue(testUserId);

        // Then
        assertThat(categories).hasSize(3); // 3 test categories for this user
        assertThat(categories).allMatch(cat -> cat.getUserId().equals(testUserId));
        assertThat(categories).allMatch(Category::getIsActive);
    }

    @Test
    void shouldFindGlobalCategories() {
        // When
        List<Category> globalCategories = categoryRepository.findGlobalCategories();

        // Then
        assertThat(globalCategories).hasSize(1); // 1 global category
        assertThat(globalCategories).allMatch(cat -> cat.getUserId() == null);
        assertThat(globalCategories).allMatch(Category::getIsActive);
    }

    @Test
    void shouldFindCategoryByUserIdAndId() {
        // Given
        UUID categoryId = UUID.fromString("66666666-6666-6666-6666-666666666666");

        // When
        Category category = categoryRepository.findByUserIdAndId(testUserId, categoryId);

        // Then
        assertThat(category).isNotNull();
        assertThat(category.getId()).isEqualTo(categoryId);
        assertThat(category.getUserId()).isEqualTo(testUserId);
        assertThat(category.getName()).isEqualTo("Food");
    }

    @Test
    void shouldReturnNullForNonExistentCategory() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Category category = categoryRepository.findByUserIdAndId(testUserId, nonExistentId);

        // Then
        assertThat(category).isNull();
    }

    @Test
    void shouldCheckIfCategoryExists() {
        // Given
        UUID existingCategoryId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        UUID nonExistentCategoryId = UUID.randomUUID();

        // When
        boolean exists = categoryRepository.existsByUserIdAndId(testUserId, existingCategoryId);
        boolean notExists = categoryRepository.existsByUserIdAndId(testUserId, nonExistentCategoryId);

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldCheckIfTransactionsExistByCategoryId() {
        // Given
        UUID categoryWithTransactions = UUID.fromString("66666666-6666-6666-6666-666666666666");
        UUID categoryWithoutTransactions = UUID.randomUUID();

        // When
        boolean hasTransactions = categoryRepository.existsTransactionsByCategoryId(categoryWithTransactions);
        boolean noTransactions = categoryRepository.existsTransactionsByCategoryId(categoryWithoutTransactions);

        // Then
        assertThat(hasTransactions).isTrue();
        assertThat(noTransactions).isFalse();
    }

    @Test
    void shouldFindCategoryByUserIdAndName() {
        // Given
        String categoryName = "Food";

        // When
        Optional<Category> category = categoryRepository.findByUserIdAndName(testUserId, categoryName);

        // Then
        assertThat(category).isPresent();
        assertThat(category.get().getName()).isEqualTo(categoryName);
        assertThat(category.get().getUserId()).isEqualTo(testUserId);
    }

    @Test
    void shouldReturnEmptyForNonExistentCategoryName() {
        // Given
        String nonExistentName = "NonExistentCategory";

        // When
        Optional<Category> category = categoryRepository.findByUserIdAndName(testUserId, nonExistentName);

        // Then
        assertThat(category).isEmpty();
    }

    @Test
    void shouldFindAllUserCategories() {
        // When
        List<Category> allCategories = categoryRepository.findByUserId(testUserId);

        // Then
        assertThat(allCategories).hasSize(3); // 3 test categories for this user
        assertThat(allCategories).allMatch(cat -> cat.getUserId().equals(testUserId));
    }
}
