package ru.manrovich.cashflow.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.data.repository.Repository;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(
        packages = "ru.manrovich.cashflow",
        importOptions = {
                ImportOption.DoNotIncludeTests.class,
                ImportOption.DoNotIncludeJars.class
        }
)
public class NamingConventionRulesTest {

    // --- Exceptions ---

    @ArchTest
    static final ArchRule exception_package_must_contain_only_exceptions =
            classes()
                    .that().resideInAPackage("..domain.kernel.exception..")
                    .should().haveSimpleNameEndingWith("Exception")
                    .because("В пакете domain.kernel.exception должны быть только исключения.");

    // --- Infrastructure JPA naming conventions ---

    @ArchTest
    static final ArchRule jpa_adapters_must_end_with_adapter =
            classes()
                    .that().resideInAPackage("..infrastructure.persistence.jpa.adapter..")
                    .should().haveSimpleNameEndingWith("Adapter")
                    .because("JPA-адаптеры реализуют доменные порты и должны оканчиваться на *Adapter.");

    @ArchTest
    static final ArchRule jpa_entities_must_end_with_entity =
            classes()
                    .that().resideInAPackage("..infrastructure.persistence.jpa.entity..")
                    .should().haveSimpleNameEndingWith("Entity")
                    .because("JPA-сущности инфраструктуры должны оканчиваться на *Entity.");

    @ArchTest
    static final ArchRule jpa_mappers_must_end_with_mapper =
            classes()
                    .that().resideInAPackage("..infrastructure.persistence.jpa.mapper..")
                    .should().haveSimpleNameEndingWith("Mapper")
                    .because("Мапперы домен <-> JPA должны оканчиваться на *Mapper.");

    @ArchTest
    static final ArchRule spring_data_repositories_must_end_with_repository =
            classes()
                    .that().areAssignableTo(Repository.class)
                    .and().resideInAnyPackage(
                            "..infrastructure.persistence.jpa.repository..",
                            "..infrastructure.persistence.jpa.springdata.."
                    )
                    .should().haveSimpleNameEndingWith("Repository")
                    .because("Spring Data репозитории должны оканчиваться на *Repository.");
}
