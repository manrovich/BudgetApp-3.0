package ru.manrovich.cashflow.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "ru.manrovich.cashflow",
        importOptions = {
                ImportOption.DoNotIncludeTests.class,
                ImportOption.DoNotIncludeJars.class
        }
)
public class ApiContractsArchTest {

    private static final String PKG_APP_WEB = "..application..web..";
    private static final String PKG_DOMAIN = "..domain..";
    private static final String PKG_INFRA = "..infrastructure..";

    @ArchTest
    static final ArchRule requests_must_not_depend_on_domain_or_infrastructure =
            noClasses()
                    .that().resideInAPackage(PKG_APP_WEB)
                    .and().haveSimpleNameEndingWith("Request")
                    .should().dependOnClassesThat().resideInAnyPackage(PKG_DOMAIN, PKG_INFRA);

    @ArchTest
    static final ArchRule responses_must_not_depend_on_domain_or_infrastructure =
            noClasses()
                    .that().resideInAPackage(PKG_APP_WEB)
                    .and().haveSimpleNameEndingWith("Response")
                    .should().dependOnClassesThat().resideInAnyPackage(PKG_DOMAIN, PKG_INFRA);

    @ArchTest
    static final ArchRule requests_must_not_use_uuid_type =
            noClasses()
                    .that().resideInAPackage(PKG_APP_WEB)
                    .and().haveSimpleNameEndingWith("Request")
                    .should().dependOnClassesThat().haveNameMatching("java\\.util\\.UUID");

    @ArchTest
    static final ArchRule responses_must_not_use_uuid_type =
            noClasses()
                    .that().resideInAPackage(PKG_APP_WEB)
                    .and().haveSimpleNameEndingWith("Response")
                    .should().dependOnClassesThat().haveNameMatching("java\\.util\\.UUID");

    @ArchTest
    static final ArchRule requests_should_be_records =
            classes()
                    .that().resideInAPackage(PKG_APP_WEB)
                    .and().haveSimpleNameEndingWith("Request")
                    .should(beARecord());

    @ArchTest
    static final ArchRule responses_should_be_records =
            classes()
                    .that().resideInAPackage(PKG_APP_WEB)
                    .and().haveSimpleNameEndingWith("Response")
                    .should(beARecord());

    private static ArchCondition<JavaClass> beARecord() {
        return new ArchCondition<>("be a record") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                boolean ok = item.isRecord();
                String message = item.getName() + " must be a record";
                events.add(new SimpleConditionEvent(item, ok, message));
            }
        };
    }
}
