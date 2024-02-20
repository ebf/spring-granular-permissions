/**
 * Copyright 2009-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ebf.security.guard;

import com.ebf.security.annotations.Permission;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.MethodClassKey;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry that would resolve and cache the permission name from the {@link MethodInvocation}
 * that is intercepted by the configured authorization method interceptor.
 */
@Slf4j
final class PermissionSecurityAttributeRegistry {

    private final Map<MethodClassKey, Collection<String>> cache = new ConcurrentHashMap<>();

    Collection<String> get(MethodInvocation invocation) {
        final Object target = invocation.getThis();
        final Class<?> targetClass = (target != null) ? target.getClass() : null;

        return get(invocation.getMethod(), targetClass);
    }

    Collection<String> get(Method method, Class<?> targetClass) {
        final MethodClassKey key = new MethodClassKey(method, targetClass);
        return this.cache.computeIfAbsent(key, (k) -> resolve(method, targetClass));
    }

    private Collection<String> resolve(Method method, Class<?> targetClass) {
        final Set<String> permissions = findPermission(method, targetClass);

        if (CollectionUtils.isEmpty(permissions)) {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(permissions);
    }

    private Set<String> findPermission(Method method, Class<?> targetClass) {
        // The method may be on an interface, like on a Repository interface...
        final Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);

        Permission annotation = AnnotationUtils.findAnnotation(specificMethod, Permission.class);
        if (annotation != null) {
            return extractValue(annotation, specificMethod);
        }

        // Check the original (e.g. interface) method
        if (specificMethod != method) {
            annotation = AnnotationUtils.findAnnotation(method, Permission.class);

            if (annotation != null) {
                return extractValue(annotation, method);
            }
        }

        // Check the class-level (note declaringClass, not targetClass, which may not actually implement the method)
        annotation = AnnotationUtils.findAnnotation(specificMethod.getDeclaringClass(), Permission.class);
        if (annotation != null) {
            return extractValue(annotation, specificMethod.getDeclaringClass());
        }

        return null;
    }

    private Set<String> extractValue(Permission annotation, AnnotatedElement element) {
        Set<String> permissions = new HashSet<>();
        final String[] values = annotation.value();

        if (log.isDebugEnabled()) {
            log.debug("{} annotation found on element {} with value: '{}'",
                    Permission.class.getSimpleName(), element.toString(), Arrays.toString(values)
            );
        }

        Arrays.stream(values).forEach(permission -> {
            Assert.state(StringUtils.hasText(permission), String.format(
                    "%s annotation that is present on the %s has a permission value that is blank.",
                    Permission.class.getSimpleName(), element.toString())
            );

            permissions.add(permission);
        });


        return permissions;
    }
}
