package ru.manrovich.cashflow.infrastructure.mapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface EntityDomainMapper<E, D> {

    D toDomain(E entity);

    E toEntity(D domain);

    default List<D> toDomainList(Collection<E> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    default List<E> toEntityList(Collection<D> domains) {
        if (domains == null) return List.of();
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
