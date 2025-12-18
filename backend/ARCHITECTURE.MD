### 0. Цель

Проект строится по DDD + Clean/Hexagonal. Мы фиксируем:

* единый язык (UL)
* слои и зависимости
* правила по пакетам и именованию
* как добавлять новый сценарий/эндпойнт без деградации архитектуры

---

## 1. Слои и зависимости

### 1.1. Слои

* **domain** — бизнес-модель, инварианты, доменные правила, порты.
* **application** — use-cases (сценарии), оркестрация, транзакции, маппинг web ↔ application.
* **infrastructure** — JPA, SpringData, внешние интеграции, реализации портов, конфигурация.

### 1.2. Правило зависимостей (запрещено нарушать)

* `domain` **не зависит** ни от чего (ни Spring, ни JPA, ни web).
* `application` зависит от `domain`.
* `infrastructure` зависит от `domain` и подключает Spring/JPA.

---

## 2. Пакеты и назначение

### 2.1. application

* Каждый бизнес-сценарий оформляется как **UseCase**.
* UseCase принимает **Command** и возвращает **Result**.
* Web слой принимает **Request** и возвращает **Response**.

Структура сценария:

```
application/<context>/usecase/<scenario>
  *Command
  *Result
  *UseCase
  *Service (impl)
```

Web слой:

```
application/<context>/web
  *Controller
  dto/*Request, *Response
  mapper/*WebMapper
```

**Важно:** Jackson-аннотации, валидация `@Valid`, форматы дат — это web-уровень, не domain.

---

### 2.2. domain

* `kernel` — общие value objects и базовые исключения.
* `model` — агрегаты/сущности/VO.
* `port`:

    * `Repository` — write-side (агрегаты)
    * `QueryPort` — read-side (проекции/проверки/существование)
* `policy` — межагрегатные доменные правила, требующие внешних данных.

**Инварианты:**

* Внутри-агрегатные правила — в модели/VO.
* Межагрегатные правила — policy + QueryPort.

---

### 2.3. infrastructure

* `entity` — JPA entities (не доменные).
* `springdata` — Spring Data репозитории.
* `adapter` — реализации доменных портов (`*RepositoryAdapter`, `*QueryPortAdapter`).
* `mapper` — домен ↔ JPA.

---

## 3. Нейминг

* Use-case интерфейс: `CreateWalletUseCase`
* Реализация: `CreateWalletService`
* Команда/результат: `CreateWalletCommand`, `CreateWalletResult`
* Порты чтения: `WalletQueryPort`, `CurrencyQueryPort`
* Порты записи: `WalletRepository`, `CurrencyRepository`
* Инфра-адаптер: `WalletRepositoryAdapter`, `WalletQueryPortAdapter`
* Политика: `WalletDeletionPolicy`

**Запрещено:** суффикс `Impl` (вместо него смысловой `Service`, `Adapter`).

---

## 4. DTO и маппинг

Цепочка данных:

* Web: `Request` ↔ `Response`
* Application: `Command` ↔ `Result`
* Domain: модели
* Infrastructure: JPA entities

Маппинг:

* `Request -> Command` — `*WebMapper`
* `Result -> Response` — `*WebMapper`
* Domain ↔ JPA — `*JpaMapper` внутри infrastructure

**Правило:** доменные модели не сериализуются напрямую в HTTP.

---

## 5. Транзакции

* Транзакции открываются **в application service** (`@Transactional`).
* domain не знает о транзакциях.

---

## 6. Исключения и обработка ошибок

* Все доменные ошибки наследуются от `DomainException`.
* Нарушение инвариантов — `ValidationException`.
* “Не найдено” — `NotFoundException` (допускается как доменное или application-level, но используется единообразно).
* Web слой маппит исключения в HTTP коды через `RestExceptionHandler`.

---

## 7. Правила дисциплины

* Контроллеры не содержат бизнес-логики (только маппинг + вызов use-case).
* Репозитории/QueryPorts не вызываются напрямую из web.
* Все команды изменения состояния проходят через use-case.
* Policy используется там, где правило требует внешних данных.

### 7.1. Запрещённые зависимости и анти-паттерны (hard rules)

Этот раздел существует, чтобы не скатиться в проблемы классической слоистой архитектуры
(“акторная сетка сервисов”, циклы зависимостей, размытие границ и тестовая боль).

#### A) UseCase не вызывает UseCase
**Запрещено:**
- `CreateWalletUseCase` вызывает `CreateTransactionUseCase`
- use-case импортирует Command/Result другого use-case и использует его как “внутренний API”

**Почему:**
это превращает application слой в “сетку акторов”, где сценарии зависят друг от друга,
появляются циклы и трудно прогнозировать эффекты изменений.

**Как правильно:**
- общий бизнес-фрагмент переносим в `domain` (policy/domain service) или в `application/common` как helper (без “usecase API”)
- либо используем события/очередь (позже), но не синхронные вызовы use-case’ов

---

#### B) Web слой не знает о доменных моделях/портах/политиках
**Запрещено в `application..web..`:**
- импорт `domain..model..` (Wallet, Transaction и т.д.)
- импорт `domain..port..` (репозитории/QueryPort)
- импорт `domain..policy..`

**Почему:**
контроллеры начинают “рулить бизнесом”, появляются неконтролируемые зависимости и
невозможность менять transport без риска сломать домен.

**Как правильно:**
- Web работает только с `Request/Response` и маппит их в `Command/Result`
- все обращения к домену — через use-case

**Исключение:**
Web может ссылаться на `domain.kernel.exception..` (для единообразного маппинга ошибок).

---

#### C) Application слой не зависит от Infrastructure
**Запрещено в `application..`:**
- импорт `infrastructure..*` (адаптеров, jpa-entity, springdata repo и т.д.)

**Почему:**
application должен зависеть только от доменных портов, иначе слой “процессов”
становится привязан к конкретной реализации хранения/интеграций.

**Как правильно:**
application использует только `domain..port..` интерфейсы, а реализации живут в `infrastructure..adapter..`.

---

#### D) Domain модели разных контекстов не зависят друг от друга напрямую
**Запрещено:**
- `domain.transaction.model.*` импортирует `domain.wallet.model.*`
- `domain.wallet.model.*` импортирует `domain.reference.category.model.*`, и т.д.

**Почему:**
прямые связи “модель-к-модели” между контекстами быстро создают циклы и “большой шар грязи”.

**Как правильно:**
межконтекстные зависимости — через:
- `QueryPort` (read-side) / `Port` (внешние действия),
- `Id` value objects из `domain.kernel.id`,
- (в перспективе) доменные события.

---

### 7.2. Как держать инварианты (чтобы их нельзя было “забыть”)

Инварианты делятся на 2 типа:

1) **Внутри агрегата / VO (strong invariants)**  
   Живут в `model`/`kernel` и всегда проверяются там:
    - `Money != 0`
    - `WalletName not blank`
    - “валюта кошелька неизменяема” (нет метода changeCurrency)

2) **Межагрегатные правила (cross-aggregate rules)**  
   Требуют внешних данных → живут в `policy` и используют `QueryPort`:
    - “нельзя удалить кошелёк если есть транзакции/не нулевой баланс”
    - “нельзя удалить категорию если она используется”

**Правило дисциплины:** операции изменения состояния выполняются только через use-case.
Это гарантирует, что policy всегда будет применён.

---

## 8. Enforcement (бетон)

Архитектурные запреты проверяются ArchUnit тестами:

- domain не зависит от Spring/JPA/application/infrastructure
- application не зависит от infrastructure (включая jpa entities)
- usecase не зависит от web (и от spring-web/http/servlet)
- web не зависит от domain model/port/policy и от infrastructure
- infrastructure не зависит от application
- domain модели разных контекстов не зависят друг от друга напрямую

### 8.1. ArchUnit как архитектурный надзор (бетон)

ArchUnit правила — это исполняемая версия наших архитектурных договорённостей.
Правила падают на этапе `check`, до ревью, чтобы архитектурный долг не копился.

#### Принципы написания правил
1) Каждое правило обязано иметь `because("...")` — объяснение зачем запрет.
2) Правила должны быть максимально устойчивыми:
    - проверяем зависимости слоёв и контекстов, а не “точные имена классов”;
    - минимизируем “магические строки” путей;
    - исключения добавляем только с обоснованием.
3) Исключения допускаются только:
    - с комментарием “почему”;
    - с ссылкой на задачу/тикет на устранение (если исключение временное).

#### “Бетон” против деградации
ArchUnit защищает нас от ключевых анти-паттернов:
- UseCase не должен вызывать UseCase (не превращаем application в “акторную сеть”).
- Web не должен знать доменные модели/порты/политики (только Request/Response + mapping).
- Application не должен зависеть от Infrastructure.
- Domain не зависит от Spring/JPA и внешних слоёв.
- Shared kernel (`domain.kernel`) не зависит от feature-доменов.
- `application.common` не зависит от feature-пакетов.

#### Запуск
Правила запускаются обычным способом:
- `./gradlew test` (или `./gradlew check`)
  и должны выполняться в CI как обязательная проверка.


## 9. Как добавить новый сценарий (чеклист)

1. Создать `Command/Result/UseCase/Service` в `application/<context>/usecase/<scenario>`
2. Добавить/использовать нужные порты в `domain/<context>/port`
3. Реализовать порты в `infrastructure/.../adapter`
4. Добавить контроллер/Request/Response/mapper в `application/<context>/web`
5. Добавить тесты:

    * unit на домен (инварианты)
    * integration на use-case (если нужно)