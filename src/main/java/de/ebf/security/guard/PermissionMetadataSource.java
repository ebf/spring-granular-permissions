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
package de.ebf.security.guard;

import de.ebf.security.annotations.Permission;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

public class PermissionMetadataSource extends AbstractMethodSecurityMetadataSource {

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
        final String permission = findPermission(method, targetClass);

        if (permission == null) {
            return null;
        }

        return Collections.singleton(
                new PermissionSecurityAttribute(permission)
        );
    }

    private String findPermission(Method method, Class<?> targetClass) {
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

    private String extractValue(Permission annotation, AnnotatedElement element) {
        final String value = annotation.value();

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("%s annotation found on element %s with value: '%s'",
                    Permission.class.getSimpleName(), element.toString(), value
            ));
        }

        Assert.state(StringUtils.hasText(value), String.format(
                "%s annotation that is present on the %s has a permission value that is blank.",
                Permission.class.getSimpleName(), element.toString())
        );

        return value;
    }
}
