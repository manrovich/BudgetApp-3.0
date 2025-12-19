# TESTS.md

Этот документ описывает принятый подход к тестированию в проекте **BudgetApp3 (backend)**: виды тестов, правила именования и расположения, соглашения по Spring/Testcontainers, а также практические рецепты “как запускать” и “что делать, если стало медленно”.

> Цель документа: чтобы тесты оставались **быстрыми**, **предсказуемыми** и **легко поддерживаемыми**, а структура не расползалась со временем.

---

## 1. Принципы

1) **Пирамида тестов**
- **Много** юнит-тестов (domain/kernel, чистая логика, без Spring).
- **Меньше** интеграционных тестов (persistence: JPA + Postgres/Testcontainers).
- **Немного** web/contract тестов (контракты API, trace-id, ошибки).

2) **Тесты проверяют контракт, а не реализацию**
- Тестируем публичные методы/интерфейсы и наблюдаемый эффект.
- В интеграционных тестах — эффект в БД (persist/find/constraints), а не внутренние детали маппинга.

3) **Изоляция данных**
- Для unit/service/web тестов: изоляция через моки/фейки.
- Для persistence: изоляция через транзакции/очистку/создание новых сущностей (UUID), без зависимости от порядка запуска.

4) **Минимальная область загрузки Spring**
- Не поднимать весь `@SpringBootTest`, если достаточно `@DataJpaTest` / slice.
- В интеграционных тестах импортировать только то, что нужно (`@Import` адаптеров/мапперов).

---

## 2. Классификация тестов в проекте

### 2.1 Domain / Kernel unit tests (без Spring)
Расположение: `src/test/java/.../domain/...`

Примеры:
- `DomainPreconditionsTest`
- `CategoryIdTest`, `CurrencyIdTest`
- `MoneyTest`, `CategoryNameTest`
- `CategoryTest`, `CurrencyTest`

Свойства:
- Быстрые (миллисекунды).
- Не используют Spring.
- Проверяют invariants и валидацию доменной модели.

**Правило:** всё, что можно проверить без I/O — должно быть проверено без I/O.

---

### 2.2 Application/service tests (use cases, сервисы) — “юнит” уровня application
Расположение: `src/test/java/.../application/...`

Примеры:
- `CreateCategoryServiceTest`
- `SeedCurrenciesServiceTest`

Свойства:
- Обычно без Spring, либо минимальный контекст.
- Порты заменяются на **in-memory fakes** или **Mockito** (см. раздел 7).

---

### 2.3 Web contract tests
Расположение: `src/test/java/.../web/...`

Примеры:
- `CategoryControllerWebContractTest`
- `CurrencyAdminControllerWebContractTest`

Свойства:
- Проверяют HTTP-контракт: коды ответов, заголовки (trace header), формат тела ошибки.
- Не должны быть “end-to-end” с реальной БД (если это не требуется контрактом).

---

### 2.4 Persistence integration tests (JPA + PostgreSQL через Testcontainers)
Расположение: `src/test/java/.../infrastructure/persistence/...`

Примеры:
- `CategoryPersistenceIntegrationTest`
- `CurrencyPersistenceIntegrationTest`

Свойства:
- Поднимают Postgres в Docker.
- Используют JPA slice (`@DataJpaTest`) + импорты адаптеров и мапперов.
- Проверяют:
  - round-trip доменной модели (save/find/exists),
  - ограничения БД (NOT NULL / длины / уникальности и т.п.).

---

### 2.5 Architecture / Naming convention tests
Расположение: `src/test/java/.../architecture/...`

Примеры:
- `ArchitectureRulesTest`
- `NamingConventionRulesTest`

Свойства:
- Не используют Spring.
- Страхуют архитектурные договоренности (ArchUnit).

---

## 3. Конвенции именования

### 3.1 Имена тест-классов
- `SomethingTest` — unit/service.
- `SomethingWebContractTest` — web contract.
- `SomethingPersistenceIntegrationTest` — JPA + Postgres/Testcontainers.
- `ArchitectureRulesTest`, `NamingConventionRulesTest` — архитектурные правила.

### 3.2 Имена тест-методов
Рекомендуемый стиль:

`methodUnderTest_shouldExpectedBehavior_whenCondition()`

Примеры:
- `save_and_findById_shouldRoundTripDomainModel()`
- `shouldThrowValidation_whenNameIsBlank()`
- `create_shouldReturn400_whenUseCaseThrowsValidation_andTraceIdInBodyMatchesHeader()`

---

## 4. Как запускать тесты

### 4.1 Gradle
- Все тесты: `./gradlew test`
- Только persistence пакет: `./gradlew test --tests "ru.manrovich.cashflow.infrastructure.persistence.*"`

> В IDE запуск “ПКМ по модулю test → Run tests” обычно эквивалентен Gradle task `test`.

### 4.2 Рекомендация на будущее: разделить unit и integration
Когда интеграционных тестов станет больше, стоит отделить их от `test`:
- пометить интеграционные тесты `@Tag("integration")`
- завести отдельный Gradle task `integrationTest`

(Это снизит время стандартного прогона unit-тестов.)

---

## 5. Persistence integration: стандартная настройка

### 5.1 Базовый класс с Testcontainers
Файл: `AbstractPostgresIntegrationTest`

```java
@Testcontainers
public abstract class AbstractPostgresIntegrationTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("cashflow_test")
                    .withUsername("test")
                    .withPassword("test");
}
```

**Пояснения:**
- `static final` + `@Container` → один контейнер на тест-класс (и может переиспользоваться между классами при правильном контексте).
- `@ServiceConnection` (Spring Boot 3.1+) → автоматическая привязка datasource к контейнеру (без ручного `@DynamicPropertySource`).
- `@SuppressWarnings("resource")` → подавляет ложный warning IDE про `AutoCloseable` (Testcontainers сам управляет жизненным циклом).

---

### 5.2 Метка-аннотация `@JpaIntegrationTest`
В проекте удобно иметь **мета-аннотацию**, чтобы не дублировать настройки.

Рекомендуемая форма:

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public @interface JpaIntegrationTest {}
```

> Важно: `replace = NONE` говорит Spring не подменять datasource на embedded.

---

### 5.3 Интеграционный тест persistence (пример)
```java
@JpaIntegrationTest
@Import({CategoryRepositoryAdapter.class, CategoryQueryPortAdapter.class, CategoryEntityMapper.class})
class CategoryPersistenceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired CategoryRepositoryAdapter categoryRepositoryAdapter;
    @Autowired CategoryQueryPortAdapter categoryQueryPortAdapter;
    @Autowired CategoryJpaRepository categoryJpaRepository;

    @Test
    void save_and_findById_shouldRoundTripDomainModel() {
        // ...
    }
}
```

**Правило:** импортируй только то, что реально нужно тесту.  
(Это влияет на скорость и стабильность контекста.)

---

## 6. Spring TestContext cache и @DirtiesContext

### 6.1 Что происходит
Spring кеширует `ApplicationContext` между тестами, чтобы не поднимать его заново.

Если в логах видно `missCount` растёт, а `size` увеличивается — значит контекст для разных тест-классов считается разным (по аннотациям/конфигурации).

### 6.2 Когда помогает @DirtiesContext
`@DirtiesContext` принудительно помечает контекст “грязным” и заставляет пересоздать его.

Это может “починить” проблемы, когда:
- тест/контекст мутирует состояние бинов,
- некорректно шарятся статические ресурсы,
- есть конфликтующие настройки.

**Минус:** часто делает прогон **заметно медленнее**.

### 6.3 Рекомендация
- Используй `@DirtiesContext` **как аварийный фикс**, но старайся в итоге убрать, выровняв конфигурацию так, чтобы контекст шарился корректно.
- Если без `@DirtiesContext` возникают флаки/зависания — это сигнал проверить:
  - одинаковость аннотаций (`@JpaIntegrationTest`, профили, `@Import`),
  - отсутствие лишних `@MockBean`/`@TestPropertySource` в одном из классов,
  - влияние статических singleton-ресурсов.

---

## 7. Fakes vs Mockito для портов

### 7.1 In-memory fakes
Плюсы:
- читаемо, устойчиво,
- легко отлаживать,
- отражают доменные сценарии, а не “вызвали метод — вернули значение”.

Минусы:
- нужно писать код фейка (но часто это окупается).

### 7.2 Mockito
Плюсы:
- быстро сделать проверку коллабораций (вызов/параметры).
- удобен, когда порт сложный, а тест проверяет 1–2 взаимодействия.

Минусы:
- тесты могут начать проверять реализацию вместо контракта,
- много `when/thenReturn` быстро превращается в “шум”.

**Правило проекта (рекомендация):**
- Для простых портов и сценариев доменной логики — **fakes**.
- Для редких коллабораций/edge-case — **Mockito**.

---

## 8. Производительность

Даже если тестовые методы выполняются быстро, много времени съедают:
- запуск Docker/Testcontainers (Ryuk + Postgres),
- поднятие Spring контекста (даже slice — это сотни миллисекунд/секунды),
- и особенно **повторный** подъем контекста/контейнера.

### 8.1 Быстрые улучшения
1) Убедиться, что persistence-тесты:
- используют один и тот же `@JpaIntegrationTest`
- не отличаются по профилям/настройкам
- не имеют разных `@TestPropertySource`

2) Не злоупотреблять `@DirtiesContext`.

3) Опционально (локально): включить reuse контейнеров Testcontainers
- в `~/.testcontainers.properties`:
  - `testcontainers.reuse.enable=true`
- и на контейнере:
  - `.withReuse(true)`

> Это ускоряет локальный цикл, но требует дисциплины (контейнеры будут жить дольше).

---

## 9. Типовые проблемы и решения

### 9.2 Долгое завершение билда / таймауты на shutdown
Если видишь в конце лога:
- `Connection is not available, request timed out after 30001ms`
- ошибки при destroy `entityManagerFactory`

Это часто означает, что Postgres-контейнер уже остановлен, а Hibernate на shutdown пытается обратиться к соединению.

Проверить:
- нет ли “двойного” контейнера (контейнер создаётся дважды, если контекст/аннотации заставляют стартовать новый),
- не перебит ли контекст `@DirtiesContext` так, что он пересоздаёт окружение,
- нет ли конфликтов `@ServiceConnection` vs ручные `@DynamicPropertySource`.

Практика:
- стараться, чтобы **контейнер был один** и поднимался предсказуемо,
- и чтобы Spring-context переиспользовался.

---

## 10. Чек-лист добавления нового persistence integration test

1) Класс в `ru.manrovich.cashflow.infrastructure.persistence` (или подпакете).
2) Наследуется от `AbstractPostgresIntegrationTest`.
3) Аннотирован `@JpaIntegrationTest`.
4) Импортирует только нужные адаптеры/мапперы через `@Import`.
5) Тесты:
   - один round-trip доменной модели (save/find/exists),
   - 1–2 теста на DB constraints (NOT NULL / длины / уникальность),
   - без зависимости от порядка выполнения.

---

## 11. Что “не делаем”

- Не используем `@SpringBootTest` для persistence, если достаточно `@DataJpaTest`.
- Не превращаем web contract тесты в E2E (если контракт можно проверить дешевле).
- Не держим интеграционные тесты в большом количестве без разделения на `integrationTest` task (когда проект вырастет).

---

## 12. Быстрые ссылки по коду (ориентиры)

- `testing.persistence.AbstractPostgresIntegrationTest`
- `testing.persistence.JpaIntegrationTest`
- `infrastructure.persistence.*PersistenceIntegrationTest`
- `architecture.*RulesTest`
- `domain.*Test`
- `web.*WebContractTest`

---

### История изменений
- 2025-12-20: зафиксировали текущий набор тестов и соглашения по Testcontainers/@ServiceConnection, кешированию контекста и `@DirtiesContext`.
