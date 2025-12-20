package ru.manrovich.cashflow.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Архитектурные запреты.
 *
 * Цели:
 * - не допустить протекание инфраструктуры/фреймворков в домен и application
 * - не допустить "акторную сетку" use-case'ов (UseCase -> UseCase)
 * - сохранить чистые границы слоёв и доменных контекстов
 *
 * Все правила должны иметь because(...) и быть максимально устойчивыми к рефакторингу.
 */
@AnalyzeClasses(
        packages = "ru.manrovich.cashflow",
        importOptions = {
                ImportOption.DoNotIncludeTests.class,
                ImportOption.DoNotIncludeJars.class
        }
)
public class ArchitectureRulesTest {

    private static final String PKG_DOMAIN = "..domain..";
    private static final String PKG_APPLICATION = "..application..";
    private static final String PKG_INFRA = "..infrastructure..";

    private static final String PKG_WEB = "..application..web..";
    private static final String PKG_USECASE = "..application..usecase..";

    private static final String PKG_DOMAIN_KERNEL = "..domain.kernel..";

    // ---------------------------
    // 1) Domain isolation
    // ---------------------------

    @ArchTest
    static final ArchRule domain_must_not_depend_on_spring_jpa_or_outer_layers =
            noClasses()
                    .that().resideInAPackage(PKG_DOMAIN)
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.springframework..",
                            "jakarta.persistence..",
                            PKG_APPLICATION,
                            PKG_INFRA
                    )
                    .because("Домен должен быть чистым: никаких зависимостей на фреймворки и внешние слои.");

    // Shared Kernel должен быть truly shared: не тянуть feature-домены
    @ArchTest
    static final ArchRule domain_kernel_must_not_depend_on_feature_domains =
            noClasses()
                    .that().resideInAPackage(PKG_DOMAIN_KERNEL)
                    .should().dependOnClassesThat().resideInAnyPackage( // TODO придумать более общую маску
                            "..domain.wallet..",
                            "..domain.transaction..",
                            "..domain.reference.."
                    )
                    .because("Shared Kernel (domain.kernel) — общий фундамент. "
                            + "Он не должен зависеть от feature-доменов, иначе появятся циклы и размывание границ.");

    // ---------------------------
    // 2) Application isolation
    // ---------------------------

    @ArchTest
    static final ArchRule application_must_not_depend_on_infrastructure =
            noClasses()
                    .that().resideInAPackage(PKG_APPLICATION)
                    .should().dependOnClassesThat().resideInAPackage(PKG_INFRA)
                    .because("Application слой (process/use-case) должен зависеть только от доменных портов, "
                            + "но не от конкретных инфраструктурных реализаций.");

    @ArchTest
    static final ArchRule application_must_not_depend_on_infrastructure_jpa_entities =
            noClasses()
                    .that().resideInAPackage(PKG_APPLICATION)
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure.persistence.jpa.entity..")
                    .because("JPA entities — деталь инфраструктуры. Application слой не должен их видеть.");

    // UseCase не должен зависеть от Web/Transport
    @ArchTest
    static final ArchRule usecases_must_not_depend_on_web_or_transport =
            noClasses()
                    .that().resideInAPackage(PKG_USECASE)
                    .should().dependOnClassesThat().resideInAnyPackage(
                            PKG_WEB,
                            "org.springframework.web..",
                            "org.springframework.http..",
                            "jakarta.servlet.."
                    )
                    .because("Use-case слой — это бизнес-процессы. Он не должен знать про HTTP/Servlet/Web.");

    // UseCase не должен вызывать/зависеть от других UseCase (защита от 'акторной сети')
    @ArchTest
    static final ArchRule usecase_packages_must_not_depend_on_each_other =
            slices()
                    .matching("..application..usecase.(*)..") // TODO вынести в константы
                    .should().notDependOnEachOther()
                    .because("UseCase пакеты не должны зависеть друг от друга: иначе application превращается в сеть взаимных вызовов. "
                            + "Общую логику выносим в domain policy/service или application.common.");

    // application.common должен быть однонаправленным (common не тянет feature-пакеты)
    @ArchTest
    static final ArchRule application_common_must_not_depend_on_feature_application_packages =
            noClasses()
                    .that().resideInAPackage("..application.common..")
                    .should().dependOnClassesThat().resideInAnyPackage( // TODO придумать более общую маску
                            "..application.wallet..",
                            "..application.transaction..",
                            "..application.reference.."
                    )
                    .because("application.common — общий слой утилит/инфраструктуры application-уровня. "
                            + "Он не должен зависеть от feature-пакетов, иначе common станет 'помойкой' и появятся циклы.");

    // ---------------------------
    // 3) Web isolation
    // ---------------------------

    // Web не должен тянуть доменные модели/порты/политики и инфраструктуру
    @ArchTest
    static final ArchRule web_must_not_depend_on_infrastructure_or_domain_model_ports_policies =
            noClasses()
                    .that().resideInAPackage(PKG_WEB)
                    .should().dependOnClassesThat().resideInAnyPackage(
                            PKG_INFRA,
                            "..domain..model..",
                            "..domain..port..",
                            "..domain..policy..",
                            // дополнительно: запретим web тянуть kernel (currencyId/money/validation/...) — кроме exception
                            "..domain.kernel.currencyId..",
                            "..domain.kernel.money..",
                            "..domain.kernel.validation..",
                            "..domain.kernel.time..",
                            "..domain.kernel.event.."
                    )
                    .because("Web слой должен быть тонким: Request/Response + mapping + вызов use-case. "
                            + "Он не должен знать доменные модели/порты/политики и детали инфраструктуры. "
                            + "DomainException (domain.kernel.exception) допускается для единого маппинга ошибок.");

    // UseCase интерфейсы должны быть интерфейсами (конвенция, снижает хаос)
    @ArchTest
    static final ArchRule usecase_types_should_be_interfaces =
            classes()
                    .that().resideInAPackage(PKG_USECASE)
                    .and().haveSimpleNameEndingWith("UseCase")
                    .should().beInterfaces()
                    .because("UseCase — контракт сценария. Реализация должна быть в *Service, а UseCase — интерфейсом.");

    @ArchTest
    static final ArchRule usecase_and_service_must_not_depend_on_request_or_response =
            noClasses()
                    .that().resideInAPackage("..application..")
                    .and().haveNameMatching(".*(UseCase|Service)$")
                    .should().dependOnClassesThat().haveNameMatching(".*(Request|Response)$")
                    .because("UseCase/Service — это application слой. Он не должен зависеть от web DTO (Request/Response).");

    @ArchTest
    static final ArchRule controller_must_not_depend_on_command_or_result =
            noClasses()
                    .that().haveNameMatching(".*Controller$")
                    .should().dependOnClassesThat().haveNameMatching(".*(Command|Result)$")
                    .because("Controller — web слой. Он не должен зависеть от application DTO (Command/Result).");

    // ---------------------------
    // 4) Infrastructure isolation
    // ---------------------------

    @ArchTest
    static final ArchRule infrastructure_must_not_depend_on_application =
            noClasses()
                    .that().resideInAPackage(PKG_INFRA)
                    .should().dependOnClassesThat().resideInAPackage(PKG_APPLICATION)
                    .because("Infrastructure реализует доменные порты. Обратной связности на application быть не должно.");

    // ---------------------------
    // 5) Domain context boundaries
    // ---------------------------

    // TODO такого пакета ещё нет
//    @ArchTest
    static final ArchRule domain_models_must_not_depend_on_each_other =
            slices()
                    .matching("..domain.(*)..model..")
                    .should().notDependOnEachOther()
                    .because("Модели разных доменных контекстов не должны зависеть друг от друга напрямую. "
                            + "Межконтекстные связи — через ports и kernel Id/VO.");

    // ---------------------------
    // 6) Cycles (опционально, но очень полезно)
    // ---------------------------

    @ArchTest
    static final ArchRule no_cycles_between_top_level_packages =
            slices()
                    .matching("ru.manrovich.cashflow.(*)..")
                    .should().beFreeOfCycles()
                    .because("Циклы зависимостей между крупными компонентами — источник деградации и сложности рефакторинга.");

    @ArchTest
    static final ArchRule shared_must_be_layer_agnostic =
            noClasses()
                    .that().resideInAPackage("..shared..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..domain..",
                            "..application..",
                            "..infrastructure..",
                            "org.springframework..",
                            "jakarta..",
                            "javax.."
                    )
                    .because("Shared — нейтральный слой для общих DTO/контейнеров. "
                            + "Он не должен зависеть от слоёв приложения/домена/инфраструктуры и от фреймворков.");

}
