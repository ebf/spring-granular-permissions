package de.ebf.security.repository;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Optional;

/**
 * @author <a href="mailto:vuk.ljubicic@ebf.com">Vuk Ljubicic</a>
 * Determines if {@link DefaultPermissionModelRepository} should be injected into context,
 * based on default.permission.model.disable property value
 * @since 26.03.20, Thu
 **/
public class DefaultPermissionModelRepositoryDisable implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Optional<String> shouldRegister =
                Optional.ofNullable(context.getEnvironment().getProperty("default.permission.model.disable"));
        return !shouldRegister.isPresent() || !("true".equalsIgnoreCase(shouldRegister.get()));
    }
}
