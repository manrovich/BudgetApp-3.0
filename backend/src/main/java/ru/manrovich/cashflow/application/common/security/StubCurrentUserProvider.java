package ru.manrovich.cashflow.application.common.security;

import org.springframework.stereotype.Component;
import ru.manrovich.cashflow.domain.kernel.id.UserId;

import java.util.UUID;

@Component
public class StubCurrentUserProvider implements CurrentUserProvider {

    private static final UserId MVP_USER = new UserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));

    @Override
    public UserId currentUserId() {
        return MVP_USER;
    }
}
