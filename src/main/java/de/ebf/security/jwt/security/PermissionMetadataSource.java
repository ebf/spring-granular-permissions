package de.ebf.security.jwt.security;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import de.ebf.security.jwt.annotations.Permission;
import de.ebf.security.jwt.annotations.ProtectedResource;

public class PermissionMetadataSource implements MethodSecurityMetadataSource {

    private static final Logger logger = LoggerFactory.getLogger(PermissionMetadataSource.class);

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        logger.info("Get Object Attributes! {} {}", object.getClass().getSimpleName());
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
        Method mostSpecificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);

        boolean isLocalRepository = AnnotationUtils.findAnnotation(mostSpecificMethod.getDeclaringClass(),
                ProtectedResource.class) != null;

        if (!isLocalRepository) {
            return null;
        }

        logger.debug("Looking up security metadata attributes for: {} on {}", method.getName(),
                targetClass.getSimpleName());

        Permission authorize = AnnotationUtils.findAnnotation(method, Permission.class);

        if (authorize == null) {
            return null;
        }

        String systemFunctionName = authorize.value();

        if (StringUtils.isEmpty(systemFunctionName)) {
            throw new IllegalStateException(String.format(
                    "Method %s with target %s marked with %s annotation but no systemFunctionName is provided.",
                    mostSpecificMethod.getName(), mostSpecificMethod.getDeclaringClass().getName(),
                    Permission.class.getSimpleName()));
        }

        logger.info("System function detected: {}", systemFunctionName);

        return Arrays.asList(new ProtectedResourceSecurityAttribute(systemFunctionName));
    }

}
