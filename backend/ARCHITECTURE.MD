# Архитектура

## 0. Назначение

Проект реализован в стиле **DDD + Clean/Hexagonal architecture**. Основная цель — сохранить управляемость кодовой базы при росте количества сценариев и доменных правил.

Документ фиксирует:

- общий словарь терминов (UL) и базовые понятия (на высоком уровне)
- слои и правила зависимостей
- соглашения по пакетам и именованию
- правила добавления новых сценариев/эндпойнтов без деградации архитектуры
- устоявшиеся соглашения по стилю кода (Lombok, DI, JPA entities)

---

## 1. Слои и зависимости

### 1.1. Слои

- **domain** — бизнес-модель, инварианты, доменные правила, порты.
- **application** — use-cases (сценарии), оркестрация, границы транзакций, маппинг transport ↔ application.
- **infrastructure** — JPA, Spring Data, внешние интеграции, реализации портов, конфигурация.

### 1.2. Правила зависимостей (hard rules)

- `domain` **не зависит** от Spring, JPA, Web, а также от `application` и `infrastructure`.
- `application` зависит только от `domain`.
- `infrastructure` зависит от `domain` и использует Spring/JPA.

---

## 2. Пакеты и ответственность

### 2.1. application

Ответственность:

- реализация **use-case** сценариев (по одному контракту на сценарий);
- определение границ транзакций (`@Transactional`);
- маппинг transport DTO ↔ use-case контракты.

Соглашения:

- сценарий оформляется как **UseCase**, который принимает **Command** и возвращает **Result**;
- web слой принимает **Request** и возвращает **Response**.

Структура сценария:
```
application/<context>/usecase/<scenario>
   *Command 
   *Result
   *UseCase
   *Service
```

Web структура:
```
application/<context>/web
   *Controller
   <scenario>/*Request, *Response, *Handler
```


Примечания:

- Jackson-аннотации, `@Valid`, ограничения форматов дат и прочие детали HTTP относятся к transport-уровню и остаются в `application..web..`.
- Use-case контракты не завязаны на HTTP и могут использоваться из других входных точек.

---

### 2.2. domain

Ответственность:

- доменная модель и инварианты;
- определение портов, через которые `application` взаимодействует с хранением и интеграциями.

Соглашения:

- `kernel` — общие value objects, идентификаторы, базовые исключения (shared kernel).
- `model` — агрегаты/сущности/value objects контекста.
- `port`:
   - `Repository` — write-side доступ к агрегатам.
   - `QueryPort` — read-side запросы/проекции/проверки существования.
- `policy` — доменные правила, которые требуют внешних данных (межагрегатные правила) и поэтому используют `QueryPort`.

Инварианты:

- **Сильные инварианты** (внутри агрегата/VO) живут в `model`/`kernel` и всегда проверяются там.
- **Межагрегатные правила** (требуют внешних данных) реализуются как `policy + QueryPort`.

---

### 2.3. infrastructure

Ответственность:

- реализация доменных портов техническими средствами (JPA/Spring Data и т.п.);
- преобразования между доменной моделью и моделью хранения.

Соглашения:

- `entity` — JPA entities (не доменные модели).
- `springdata` — Spring Data репозитории.
- `adapter` — реализации доменных портов (`*RepositoryAdapter`, `*QueryPortAdapter`).
- `mapper` — домен ↔ JPA (`*JpaMapper`).

---

## 3. Именование

- Use-case интерфейс: `CreateWalletUseCase`
- Реализация use-case: `CreateWalletService`
- Контракты: `CreateWalletCommand`, `CreateWalletResult`
- Порты чтения: `WalletQueryPort`, `CurrencyQueryPort`
- Порты записи: `WalletRepository`, `CurrencyRepository`
- Инфраструктурный адаптер: `WalletRepositoryAdapter`, `WalletQueryPortAdapter`
- Доменная политика: `WalletDeletionPolicy`

Запрещено:

- суффикс `Impl` (`*Impl`). Используются смысловые имена (`Service`, `Adapter`).

---

## 4. DTO и маппинг

Поток данных:

- Web: `Request` ↔ `Response`
- Application: `Command` ↔ `Result`
- Domain: доменные модели (агрегаты/сущности/VO)
- Infrastructure: JPA entities

Маппинг:

- `Request -> Command` — `*WebMapper`
- `Result -> Response` — `*WebMapper`
- Domain ↔ JPA — `*JpaMapper` внутри infrastructure

Правило:

- доменные модели напрямую в HTTP не сериализуются.

---

## 5. Транзакции

- Границы транзакций задаются в `application` (обычно на уровне реализации use-case через `@Transactional`).
- `domain` ничего не знает о транзакциях.

---

## 6. Исключения и обработка ошибок

- Все доменные ошибки наследуются от `DomainException`.
- Нарушение инвариантов — `ValidationException`.
- Отсутствие сущности — `NotFoundException` (допускается как доменное или application-уровня, но должно использоваться единообразно).
- Web слой маппит исключения в HTTP ответы через `RestExceptionHandler`.

---

## 7. Правила дисциплины (hard rules)

- Контроллеры не содержат бизнес-логики (только маппинг и вызов use-case).
- Репозитории/QueryPort не вызываются напрямую из web.
- Все изменения состояния выполняются только через use-case.
- `policy` применяется там, где доменное правило требует внешних данных.

### 7.1. Запрещённые зависимости и анти-паттерны

#### A) UseCase не вызывает UseCase

Запрещено:

- `CreateWalletUseCase` вызывает `CreateTransactionUseCase`
- use-case импортирует `Command/Result` другого use-case и использует их как внутренний API

Причина:

- application слой превращается в неявный граф зависимостей; изменения становятся труднее прогнозировать, возрастает риск циклов.

Как правильно:

- общий бизнес-фрагмент переносится в `domain` (policy/domain service) или в `application/common` (без связи через use-case API);
- для оркестрации допускаются события/асинхронные механизмы (при необходимости, позже).

---

#### B) Web слой не зависит от domain model/ports/policies

Запрещено в `application..web..`:

- импорт `domain..model..`
- импорт `domain..port..`
- импорт `domain..policy..`

Причина:

- контроллеры начинают управлять бизнес-процессами, появляются неконтролируемые связи.

Как правильно:

- web слой работает только с `Request/Response` и маппит их в `Command/Result`;
- взаимодействие с доменом происходит через use-case.

---

#### C) Application слой не зависит от Infrastructure

Запрещено в `application..`:

- импорт `infrastructure..*` (адаптеры, JPA entities, springdata repo и т.д.)

Причина:

- application слой должен зависеть от доменных портов, а не от конкретной реализации хранения/интеграций.

---

#### D) Доменные модели разных контекстов не зависят друг от друга напрямую

Запрещено:

- `domain.transaction.model.*` импортирует `domain.wallet.model.*`
- `domain.wallet.model.*` импортирует `domain.reference.*.model.*` и т.п.

Причина:

- прямые связи между моделями контекстов повышают связность, создают циклы и приводят к “большому шару грязи”.

Как правильно:

- межконтекстные зависимости оформляются через:
   - `Id` value objects из `domain.kernel.id`,
   - `QueryPort`/другие порты,
   - (в перспективе) доменные события.

---

### 7.2. Инварианты (как избегается “забывание правил”)

Инварианты делятся на две категории:

1) **Сильные инварианты (уровень агрегата/VO)**  
   Проверяются внутри `model`/`kernel` всегда, примеры:
   - `Money.amount != 0`
   - `WalletName` не пустой
   - валюта кошелька неизменяема (нет метода `changeCurrency`)

2) **Межагрегатные правила (нужны внешние данные)**  
   Реализуются через `policy + QueryPort`, примеры:
   - кошелёк нельзя удалить при наличии транзакций/ненулевом балансе
   - категорию нельзя удалить, если она используется

Операции изменения состояния выполняются только через use-case, что обеспечивает единообразное применение `policy`.

---

## 8. Автоматическое соблюдение архитектуры (enforcement)

Архитектурные запреты проверяются автоматизированно:

- **ArchUnit**: границы слоёв и запрещённые зависимости
- **Checkstyle** (рекомендуется): соглашения на уровне исходников (например, правила по логированию и Lombok)

### 8.1. ArchUnit (обязательно)

Типовые правила:

- `domain` не зависит от Spring/JPA/application/infrastructure
- `application` не зависит от infrastructure (включая JPA entities)
- `web` не зависит от domain model/ports/policies и от infrastructure
- `infrastructure` не зависит от `application`
- доменные модели разных контекстов не зависят друг от друга напрямую
- use-case не вызывает use-case

ArchUnit правила должны выполняться в CI в составе `./gradlew check`.

---

## 9. Как добавить новый сценарий (чеклист)

1) Создать `Command/Result/UseCase/Service` в `application/<context>/usecase/<scenario>`
2) Добавить/использовать нужные порты в `domain/<context>/port`
3) Реализовать порты в `infrastructure/.../adapter`
4) Добавить контроллер + `Request/Response` + mapper в `application/<context>/web`
5) Добавить тесты:
   - unit-тесты для доменных инвариантов
   - интеграционные тесты для use-case (по необходимости)

---

## 10. Code style (Lombok, DI, аннотации, JPA)

### 10.1. Логирование

- Логирование выполняется через Lombok `@Slf4j`.
- Явное объявление логгера через `LoggerFactory.getLogger(...)` не используется.

---

### 10.2. Dependency Injection

- Используется constructor injection.
- Поля зависимостей объявляются как `final`.
- По умолчанию используется Lombok `@RequiredArgsConstructor` (явные конструкторы пишутся только при необходимости).

Запрещено:

- field injection (`@Autowired` на полях)
- setter injection для зависимостей

---

### 10.3. Порядок аннотаций (соглашение)

Аннотации располагаются сверху вниз группами по важности:

1) framework/persistence аннотации (`@Entity`, `@Table`, `@Service`, …)
2) Lombok аннотации (`@Getter`, `@RequiredArgsConstructor`, …)
3) На полях — сначала “смысловые” (`@Column`, …), затем точечные Lombok-аннотации (`@Setter` при необходимости)

Пример:

```java
@Entity
@Table(name = "wallet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class WalletJpaEntity {

    @Column(name = "name", nullable = false, length = 64)
    @Setter
    private String name;
}
```

10.4. JPA Entities
Шаблон entity класса:
- @Entity + @Table
- @Getter на класс
- @NoArgsConstructor(access = PROTECTED) обязателен (требование JPA)
- @AllArgsConstructor допускается
- @Setter допускается только точечно на нужных полях

Запрещено:
- @Setter на уровне класса
- Lombok @Data на JPA entities