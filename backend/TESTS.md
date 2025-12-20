# Тесты

Документ описывает, как в репозитории устроены тесты, как их запускать и какие соглашения используются.

## Запуск

### Из IDE

- Запуск одного теста/класса: стандартный `Run` (JUnit 5).
- Отладка: `Debug`.
- При запуске интеграционных тестов требуется локальный Docker.

### Из командной строки

Linux/macOS:

```bash
./gradlew test
```

Windows:

```bat
gradlew.bat test
```

Запуск отдельных тестов:

```bash
./gradlew test --tests "ru.manrovich.cashflow.infrastructure.persistence.*"
```

Подробный лог:

```bash
./gradlew test --info
```

## Уровни тестов

В проекте используются несколько уровней тестов - от самых быстрых до интеграционных.

### Unit-тесты

Проверяют доменную и прикладную логику без Spring и без внешних ресурсов.

- Детерминированные и быстрые.
- Не поднимают контекст приложения.
- Используются для проверки бизнес-правил, value objects и доменных сервисов.

### Web contract тесты

Проверяют REST-контракт на уровне контроллеров: входные DTO (Bean Validation), статус-коды, формат успешных ответов и минимально — формат ошибок.

**Цель:** зафиксировать HTTP-контракт так, чтобы изменения в контроллере/маппинге/валидации ломали тесты раньше, чем фронт.

#### Что проверяем (обязательно)
1) **Happy-path**:
    - статус (200/201/204 и т.д.)
    - content-type
    - ключевые поля ответа (минимально необходимые)
    - контроллер вызывает use-case с корректным Command (verify)

2) **Bean Validation (DTO) → 400**:
    - при невалидном Request возвращается 400
    - use-case НЕ вызывается

3) **Доменные исключения → HTTP** (по необходимости на эндпойнт):
    - ValidationException → 400
    - NotFoundException → 404
    - ConflictException → 409

#### Что НЕ проверяем в каждом контрактном тесте
- сквозные компоненты (TraceIdFilter, MDC, детали логирования)
- полный набор полей ошибки/ответа (если это не критично для контракта)
- точные тексты сообщений (предпочтительно проверять структуру)

#### Сквозные тесты (отдельно, 1 раз на проект)
- TraceIdFilter: генерирует/пробрасывает X-Request-Id
- RestExceptionHandler: единый формат ApiErrorResponse
- обработка ошибок Bean Validation (MethodArgumentNotValidException/…): единый ответ 400

#### Техническая база
- Используем @WebMvcTest
- Зависимости контроллера мокируются (@MockitoBean)
- Общая web-конфигурация подключается через WebContractTestBase (RestExceptionHandler, TraceIdFilter, test-security)


### Архитектурные тесты

Проверяют структурные правила проекта (например, зависимость слоёв) с помощью ArchUnit.

- Должны быть быстрыми.
- Не требуют Spring.

### Интеграционные JPA-тесты

Проверяют слой `infrastructure/persistence`: репозитории, адаптеры портов, мапперы и ограничения БД.

- Поднимают минимальный Spring-контекст для JPA.
- Используют реальный PostgreSQL через Testcontainers.

## Интеграционные JPA-тесты с PostgreSQL + Testcontainers

### Требования

- Установлен и запущен Docker (Docker Desktop / Docker Engine).
- Доступ к Docker daemon у текущего пользователя.

### Базовый контейнер PostgreSQL

Для интеграционных тестов используется общий базовый класс:

```java
@Testcontainers
public abstract class AbstractPostgresIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("cashflow_test")
                    .withUsername("test")
                    .withPassword("test");
}
```

Важно:

- Поле `postgres` не обязано использоваться напрямую в тестах. Аннотация `@ServiceConnection` (Spring Boot) регистрирует параметры подключения автоматически, и DataSource настраивается без `@DynamicPropertySource`.
- Жизненный цикл контейнера управляется JUnit 5 / Testcontainers через `@Testcontainers` + `@Container`.

### Метка `@JpaIntegrationTest`

В проекте используется мета-аннотация `@JpaIntegrationTest` (см. `ru.manrovich.cashflow.testing.persistence`). Она инкапсулирует типичную конфигурацию для JPA-интеграционных тестов (например, `@DataJpaTest`, тестовый профиль и отключение подмены DataSource).

Это позволяет держать сами тесты компактными и одинаково настроенными.

### Подключение адаптеров и мапперов

`@DataJpaTest` по умолчанию поднимает только JPA-инфраструктуру. Для тестирования адаптеров портов и мапперов они импортируются явно:

```java
@JpaIntegrationTest
@Import({
    CategoryRepositoryAdapter.class,
    CategoryQueryPortAdapter.class,
    CategoryEntityMapper.class
})
class CategoryPersistenceIntegrationTest extends AbstractPostgresIntegrationTest {
    // ...
}
```

Такой подход держит интеграционный тест ближе к реальной конфигурации persistence-слоя и при этом избегает поднятия всего приложения.

### Сброс контекста и изоляция

Spring кэширует ApplicationContext между тестами для ускорения прогона. Иногда отдельным тестам требуется гарантированно «свежий» контекст (например, при изменении конфигурации контекста или при проблемах с корректным закрытием ресурсов).

Для этого допускается использовать `@DirtiesContext` (обычно на уровне класса). При применении аннотации стоит учитывать, что кэш контекста будет сброшен, и тесты станут выполняться медленнее.

## Соглашения по тестам

- Тесты должны быть независимыми друг от друга: без зависимостей от порядка выполнения и «остатков» состояния.
- Названия тестов описывают поведение (BDD-стиль): `method_shouldDoX_whenY`.
- Явные `flush()` в JPA-тестах используются там, где важно зафиксировать запись в БД и проверить constraint/уникальность на уровне PostgreSQL.
- Проверка DB-ограничений делается отдельными тестами, даже если доменная модель уже валидирует входные данные. Цель — убедиться, что база защищает данные на своём уровне.

## Где добавлять новые тесты

- **Domain / Application**: unit-тесты рядом с соответствующими пакетами.
- **Infrastructure (persistence)**: `@JpaIntegrationTest` + Postgres/Testcontainers.
- **Web**: контрактные тесты контроллеров.
- **Архитектура**: ArchUnit-тесты в отдельном пакете с правилами.
