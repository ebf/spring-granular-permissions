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

import de.ebf.security.annotations.Permission;
import de.ebf.security.annotations.ProtectedResource;

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
                ProtectedResource.class) != null
                || AnnotationUtils.findAnnotation(targetClass, ProtectedResource.class) != null;

        if (!isLocalRepository) {
            return null;
        }

        logger.debug("Looking up security metadata attributes for: {} on {}", method.getName(),
                targetClass.getSimpleName());

        Permission authorize = AnnotationUtils.findAnnotation(mostSpecificMethod, Permission.class);

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

        return Arrays.asList(new PermissionSecurityAttribute(systemFunctionName));
    }

}
