package ru.manrovich.cashflow.application.common.security;

import ru.manrovich.cashflow.domain.kernel.id.UserId;

public interface CurrentUserProvider {
    UserId currentUserId();
}
