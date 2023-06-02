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
package com.ebf.security.scanner;

import com.ebf.security.annotations.Permission;
import com.ebf.security.annotations.ProtectedResource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 */
public class DefaultPermissionScanner extends ClassPathScanningCandidateComponentProvider
        implements PermissionScanner, EnvironmentAware, InitializingBean {

    private Set<String> basePackageNames;

    public DefaultPermissionScanner() {
        super(false);
        addIncludeFilter(new AnnotationTypeFilter(ProtectedResource.class));
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(basePackageNames, "Base package names can not be null");
    }

    public void setBasePackageNames(Set<String> basePackageNames) {
        this.basePackageNames = basePackageNames;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isIndependent();
    }

    @NonNull
    @Override
    public Set<String> scan() {
        return basePackageNames.stream()
                .map(this::findCandidateComponents)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(this::introspect)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private @Nullable Set<String> introspect(@NonNull BeanDefinition definition) {
        if (logger.isTraceEnabled()) {
            logger.trace("Attempting to find Permission annotations on methods for " + definition);
        }

        try {
            final Class<?> clazz = Class.forName(definition.getBeanClassName());
            final Method[] declaredMethods = ReflectionUtils.getDeclaredMethods(clazz);

            final Set<String> permissions = new LinkedHashSet<>();

            for (Method systemFunctionResource : declaredMethods) {
                final Method mostSpecificMethod = ClassUtils.getMostSpecificMethod(systemFunctionResource, clazz);
                final Permission permission = AnnotatedElementUtils.findMergedAnnotation(mostSpecificMethod, Permission.class);

                if (permission == null) {
                    continue;
                }

                if (logger.isTraceEnabled()) {
                    logger.trace("Found permission " + Arrays.toString(permission.value()) + " on " + mostSpecificMethod);
                }

                Collections.addAll(permissions, permission.value());
            }

            return permissions;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
