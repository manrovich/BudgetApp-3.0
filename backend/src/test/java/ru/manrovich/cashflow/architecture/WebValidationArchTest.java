package ru.manrovich.cashflow.architecture;

import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaParameter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@AnalyzeClasses(
        packages = "ru.manrovich.cashflow",
        importOptions = {
                ImportOption.DoNotIncludeTests.class,
                ImportOption.DoNotIncludeJars.class
        }
)
public class WebValidationArchTest {

    private static final String PKG_API = "..api..";

    @ArchTest
    static final ArchRule request_body_request_parameters_must_be_validated =
            methods()
                    .that().areDeclaredInClassesThat().resideInAPackage(PKG_API)
                    .and().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
                    .or().areDeclaredInClassesThat().areAnnotatedWith(Controller.class)
                    .should(requestBodyRequestParametersMustHaveValid());

    private static ArchCondition<JavaMethod> requestBodyRequestParametersMustHaveValid() {
        return new ArchCondition<>("have @Valid (or @Validated) on @RequestBody *Request parameters") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                for (JavaParameter parameter : method.getParameters()) {
                    boolean isRequestBody = parameter.isAnnotatedWith(RequestBody.class);
                    boolean isRequestDto = parameter.getRawType().getSimpleName().endsWith("Request");

                    if (!isRequestBody || !isRequestDto) {
                        continue;
                    }

                    boolean hasValid = parameter.isAnnotatedWith(Valid.class) || parameter.isAnnotatedWith(Validated.class);

                    String message = String.format(
                            "%s#%s(..): parameter '%s' (%s) is @RequestBody *Request but is missing @Valid/@Validated",
                            method.getOwner().getName(),
                            method.getName(),
                            parameter.getIndex(),
                            parameter.getRawType().getName()
                    );

                    events.add(new SimpleConditionEvent(method, hasValid, message));
                }
            }
        };
    }
}
