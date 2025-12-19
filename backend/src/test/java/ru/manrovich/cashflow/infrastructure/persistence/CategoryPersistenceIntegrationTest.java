package ru.manrovich.cashflow.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.manrovich.cashflow.BudgetApplication;
import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.reference.category.model.Category;
import ru.manrovich.cashflow.domain.reference.category.model.CategoryName;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter.CategoryQueryPortAdapter;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.adapter.CategoryRepositoryAdapter;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.mapper.CategoryEntityMapper;
import ru.manrovich.cashflow.infrastructure.persistence.jpa.repository.CategoryJpaRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@ContextConfiguration(classes = BudgetApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        CategoryRepositoryAdapter.class,
        CategoryQueryPortAdapter.class,
        CategoryEntityMapper.class
})
class CategoryPersistenceIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("cashflow_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Liquibase выключен — схему создаёт Hibernate
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private CategoryRepositoryAdapter categoryRepositoryAdapter;

    @Autowired
    private CategoryQueryPortAdapter categoryQueryPortAdapter;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository; // flush()

    private static final UserId USER_1 = new UserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    private static final UserId USER_2 = new UserId(UUID.fromString("00000000-0000-0000-0000-000000000002"));

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
        // создадим entity напрямую через jpaRepository, чтобы обойти домен
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