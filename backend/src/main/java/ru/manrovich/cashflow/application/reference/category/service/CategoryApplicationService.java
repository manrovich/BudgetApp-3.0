package ru.manrovich.cashflow.application.reference.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.manrovich.cashflow.application.common.security.CurrentUserProvider;
import ru.manrovich.cashflow.application.reference.category.usecase.CategoryUseCase;
import ru.manrovich.cashflow.application.reference.category.usecase.command.CreateCategoryCommand;
import ru.manrovich.cashflow.application.reference.category.usecase.result.CreateCategoryResult;
import ru.manrovich.cashflow.domain.kernel.exception.ConflictException;
import ru.manrovich.cashflow.domain.kernel.id.CategoryId;
import ru.manrovich.cashflow.domain.kernel.id.UserId;
import ru.manrovich.cashflow.domain.reference.category.model.Category;
import ru.manrovich.cashflow.domain.reference.category.model.CategoryName;
import ru.manrovich.cashflow.domain.reference.category.port.CategoryQueryPort;
import ru.manrovich.cashflow.domain.reference.category.port.CategoryRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryApplicationService implements CategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final CategoryQueryPort queryPort;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public CreateCategoryResult create(CreateCategoryCommand command) {
        UserId ownerId = currentUserProvider.currentUserId();
        CategoryName name = new CategoryName(command.name());

        if (queryPort.existsByNameIgnoreCase(ownerId, name)) {
            throw new ConflictException("Category with name already exists: " + name.value());
        }

        Category category = new Category(
                new CategoryId(UUID.randomUUID()),
                ownerId,
                name
        );

        Category saved = categoryRepository.save(category);

        return new CreateCategoryResult(
                saved.id().value().toString(),
                saved.name().value()
        );
    }
}
