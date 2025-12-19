package ru.manrovich.cashflow.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.reference.category.model.Category;
import ru.manrovich.cashflow.domain.reference.category.model.CategoryName;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter.CategoryQueryPortAdapter;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter.CategoryRepositoryAdapter;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.mapper.CategoryEntityMapper;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import ru.manrovich.cashflow.testing.persistence.AbstractPostgresIntegrationTest;
import ru.manrovich.cashflow.testing.persistence.JpaIntegrationTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.manrovich.cashflow.testing.data.TestUsers.USER_1;
import static ru.manrovich.cashflow.testing.data.TestUsers.USER_2;

@JpaIntegrationTest
@Import({CategoryRepositoryAdapter.class,
        CategoryQueryPortAdapter.class,
        CategoryEntityMapper.class})
class CategoryPersistenceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private CategoryRepositoryAdapter categoryRepositoryAdapter;

    @Autowired
    private CategoryQueryPortAdapter categoryQueryPortAdapter;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

    @Test
    void exists_shouldReturnFalse_whenCategoryNotSaved() {
        CategoryId id = new CategoryId(UUID.randomUUID());
        assertFalse(categoryQueryPortAdapter.exists(USER_1, id));
    }

    @Test
    void save_and_findById_shouldRoundTripDomainModel() {
        CategoryId id = new CategoryId(UUID.randomUUID());
        Category category = new Category(
                id,
                USER_1,
                new CategoryName("Food")
        );

        categoryRepositoryAdapter.save(category);
        categoryJpaRepository.flush();

        assertTrue(categoryQueryPortAdapter.exists(USER_1, id));

        Category loaded = categoryRepositoryAdapter.findById(USER_1, id)
                .orElseThrow(() -> new AssertionError("Category must be found"));

        assertEquals(id.value(), loaded.id().value());
        assertEquals(USER_1.value(), loaded.ownerId().value());
        assertEquals("Food", loaded.name().value());
    }

    @Test
    void existsByNameIgnoreCase_shouldBeTrue_whenSameUserSameNameDifferentCase() {
        CategoryId id = new CategoryId(UUID.randomUUID());
        Category category = new Category(
                id,
                USER_1,
                new CategoryName("Food")
        );

        categoryRepositoryAdapter.save(category);
        categoryJpaRepository.flush();

        assertTrue(categoryQueryPortAdapter.existsByNameIgnoreCase(USER_1, new CategoryName("food")));
        assertTrue(categoryQueryPortAdapter.existsByNameIgnoreCase(USER_1, new CategoryName("FOOD")));
    }

    @Test
    void existsByNameIgnoreCase_shouldBeFalse_forAnotherUser() {
        CategoryId id = new CategoryId(UUID.randomUUID());
        Category category = new Category(
                id,
                USER_1,
                new CategoryName("Food")
        );

        categoryRepositoryAdapter.save(category);
        categoryJpaRepository.flush();

        // Другой пользователь — не должен "видеть" существование имени
        assertFalse(categoryQueryPortAdapter.existsByNameIgnoreCase(USER_2, new CategoryName("Food")));
    }

    @Test
    void deleteById_shouldDeleteOnlyForOwner() {
        CategoryId id = new CategoryId(UUID.randomUUID());

        categoryRepositoryAdapter.save(new Category(id, USER_1, new CategoryName("Food")));
        categoryJpaRepository.flush();

        // Пытаемся удалить "чужим" owner'ом — по контракту репозитория удалять не должно
        categoryRepositoryAdapter.deleteById(USER_2, id);
        categoryJpaRepository.flush();

        assertTrue(categoryQueryPortAdapter.exists(USER_1, id));

        // Удаляем правильным owner'ом
        categoryRepositoryAdapter.deleteById(USER_1, id);
        categoryJpaRepository.flush();

        assertFalse(categoryQueryPortAdapter.exists(USER_1, id));
    }

    @Test
    void dbConstraints_shouldRejectNullName() {
        // Домен не позволяет null/blank, но проверим именно DB constraint:
        var badEntity = new ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.CategoryEntity(
                UUID.randomUUID(),
                USER_1.value(),
                null
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            categoryJpaRepository.save(badEntity);
            categoryJpaRepository.flush();
        });
    }

    @Test
    void dbConstraints_shouldRejectTooLongName() {
        String tooLongName = "X".repeat(129);

        var badEntity = new ru.manrovich.cashflow.infrastructure.persistence.jpa.entity.CategoryEntity(
                UUID.randomUUID(),
                USER_1.value(),
                tooLongName
        );

        assertThrows(DataIntegrityViolationException.class, () -> {
            categoryJpaRepository.save(badEntity);
            categoryJpaRepository.flush();
        });
    }
}