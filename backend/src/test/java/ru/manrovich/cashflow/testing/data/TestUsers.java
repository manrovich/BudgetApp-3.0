package ru.manrovich.cashflow.testing.data;

import ru.manrovich.cashflow.domain.kernel.id.UserId;

import java.util.UUID;

public final class TestUsers {
    private TestUsers() {}

    public static final UserId USER_1 =
            new UserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));

    public static final UserId USER_2 =
            new UserId(UUID.fromString("00000000-0000-0000-0000-000000000002"));

    public static UserId user(int n) {
        return new UserId(UUID.fromString(String.format(
                "00000000-0000-0000-0000-%012d", n
        )));
    }
}
