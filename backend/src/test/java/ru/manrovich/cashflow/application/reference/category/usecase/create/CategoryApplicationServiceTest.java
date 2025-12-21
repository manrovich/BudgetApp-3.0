package ru.manrovich.cashflow.application.reference.category.usecase.create;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.manrovich.cashflow.application.common.security.CurrentUserProvider;
import ru.manrovich.cashflow.application.reference.category.service.CategoryApplicationService;
import ru.manrovich.cashflow.application.reference.category.usecase.command.CreateCategoryCommand;
import ru.manrovich.cashflow.application.reference.category.usecase.query.ListCategoriesQuery;
import ru.manrovich.cashflow.application.reference.category.usecase.result.CreateCategoryResult;
import ru.manrovich.cashflow.domain.kernel.exception.ConflictException;
import ru.manrovich.cashflow.domain.kernel.exception.ValidationException;
import ru.manrovich.cashflow.domain.reference.category.model.Category;
import ru.manrovich.cashflow.domain.reference.category.model.CategoryName;
import ru.manrovich.cashflow.domain.reference.category.port.CategoryQueryPort;
import ru.manrovich.cashflow.domain.reference.category.port.CategoryRepository;
import ru.manrovich.cashflow.shared.query.Slice;
import ru.manrovich.cashflow.shared.query.SlicePage;
import ru.manrovich.cashflow.shared.readmodel.CategoryListItem;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static ru.manrovich.cashflow.testing.data.TestUsers.USER_1;

@ExtendWith(MockitoExtension.class)
class CategoryApplicationServiceTest {

    @Mock
    private CategoryRepository repository;
    @Mock
    private CategoryQueryPort categoryQueryPort;
    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private CategoryApplicationService service;

    @Test
    void shouldCreateCategory_whenNameIsUnique() {
        when(currentUserProvider.currentUserId()).thenReturn(USER_1);
        when(categoryQueryPort.existsByNameIgnoreCase(eq(USER_1), any(CategoryName.class))).thenReturn(false);

        when(repository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateCategoryResult result = service.create(new CreateCategoryCommand("  Food  "));

        assertEquals("Food", result.name());
        assertNotNull(result.categoryId());
        assertFalse(result.categoryId().isBlank());

        verify(categoryQueryPort).existsByNameIgnoreCase(eq(USER_1), eq(new CategoryName("Food")));
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
        when(currentUserProvider.currentUserId()).thenReturn(USER_1);
        when(categoryQueryPort.existsByNameIgnoreCase(eq(USER_1), any(CategoryName.class))).thenReturn(true);

        assertThrows(ConflictException.class, () ->
                service.create(new CreateCategoryCommand("Food"))
        );

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowValidation_whenNameIsBlank() {
        assertThrows(ValidationException.class, () ->
                service.create(new CreateCategoryCommand("   "))
        );

        verifyNoInteractions(categoryQueryPort);
        verifyNoInteractions(repository);
    }

    @Test
    void list_shouldUseDefaults_whenPageAndSizeNull() {
        when(currentUserProvider.currentUserId()).thenReturn(USER_1);

        Slice<CategoryListItem> expected = new Slice<>(
                List.of(),
                new SlicePage(0, 200, false)
        );

        when(categoryQueryPort.findListItems(any(CategoryQueryPort.CategorySearchCriteria.class)))
                .thenReturn(expected);

        ListCategoriesQuery query = new ListCategoriesQuery(
                null,
                null,
                null
        );

        Slice<CategoryListItem> actual = service.list(query);

        assertSame(expected, actual);

        ArgumentCaptor<CategoryQueryPort.CategorySearchCriteria> captor =
                ArgumentCaptor.forClass(CategoryQueryPort.CategorySearchCriteria.class);

        verify(categoryQueryPort).findListItems(captor.capture());

        CategoryQueryPort.CategorySearchCriteria criteria = captor.getValue();
        assertEquals(USER_1, criteria.ownerId());
        assertNull(criteria.query());
        assertEquals(0, criteria.page());
        assertEquals(200, criteria.size());
    }

    @Test
    void list_shouldClampPageAndSize() {
        when(currentUserProvider.currentUserId()).thenReturn(USER_1);

        Slice<CategoryListItem> expected = new Slice<>(
                List.of(),
                new SlicePage(0, 200, false)
        );

        when(categoryQueryPort.findListItems(any(CategoryQueryPort.CategorySearchCriteria.class)))
                .thenReturn(expected);

        ListCategoriesQuery query = new ListCategoriesQuery(
                "food",
                -100,
                10_000
        );

        service.list(query);

        ArgumentCaptor<CategoryQueryPort.CategorySearchCriteria> captor =
                ArgumentCaptor.forClass(CategoryQueryPort.CategorySearchCriteria.class);

        verify(categoryQueryPort).findListItems(captor.capture());

        CategoryQueryPort.CategorySearchCriteria criteria = captor.getValue();
        assertEquals(USER_1, criteria.ownerId());
        assertEquals("food", criteria.query());
        assertEquals(0, criteria.page());
        assertEquals(200, criteria.size());
    }
}
