package ru.manrovich.cashflow.application.reference.category.usecase.create;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.manrovich.cashflow.application.common.security.CurrentUserProvider;
import ru.manrovich.cashflow.domain.kernel.exception.ConflictException;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.domain.reference.category.model.Category;
import ru.manrovich.cashflow.domain.reference.category.model.CategoryName;
import ru.manrovich.cashflow.domain.reference.category.port.CategoryQueryPort;
import ru.manrovich.cashflow.domain.reference.category.port.CategoryRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static ru.manrovich.cashflow.testing.data.TestUsers.USER_1;

class CreateCategoryServiceTest {

    private CategoryRepository repository;
    private CategoryQueryPort queryPort;

    private CreateCategoryService service;

    @BeforeEach
    void setUp() {
        repository = mock(CategoryRepository.class);
        queryPort = mock(CategoryQueryPort.class);
        CurrentUserProvider currentUserProvider = mock(CurrentUserProvider.class);

        service = new CreateCategoryService(repository, queryPort, currentUserProvider);

        when(currentUserProvider.currentUserId()).thenReturn(USER_1);
    }

    @Test
    void shouldCreateCategory_whenNameIsUnique() {
        when(queryPort.existsByNameIgnoreCase(eq(USER_1), any(CategoryName.class))).thenReturn(false);

        when(repository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateCategoryResult result = service.execute(new CreateCategoryCommand("  Food  "));

        assertEquals("Food", result.name());
        assertNotNull(result.categoryId());
        assertFalse(result.categoryId().isBlank());

        verify(queryPort).existsByNameIgnoreCase(eq(USER_1), eq(new CategoryName("Food")));
        verify(repository, times(1)).save(any(Category.class));

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(repository).save(captor.capture());

        Category saved = captor.getValue();
        assertEquals(USER_1, saved.ownerId());
        assertEquals("Food", saved.name().value());
        assertNotNull(saved.id());
    }

    @Test
    void shouldThrowConflict_whenCategoryWithSameNameAlreadyExists() {
        when(queryPort.existsByNameIgnoreCase(eq(USER_1), any(CategoryName.class))).thenReturn(true);

        assertThrows(ConflictException.class, () ->
                service.execute(new CreateCategoryCommand("Food"))
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowValidation_whenNameIsBlank() {
        assertThrows(ValidationException.class, () ->
                service.execute(new CreateCategoryCommand("   "))
        );

        verifyNoInteractions(queryPort);
        verifyNoInteractions(repository);
    }
}
