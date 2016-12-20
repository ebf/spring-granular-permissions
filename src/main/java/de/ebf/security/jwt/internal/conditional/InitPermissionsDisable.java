package de.ebf.security.jwt.internal.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class InitPermissionsDisable implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String shouldDisable = context.getEnvironment().getProperty("init.permissions.disable");
        return !("true".equalsIgnoreCase(shouldDisable));
    }

}
