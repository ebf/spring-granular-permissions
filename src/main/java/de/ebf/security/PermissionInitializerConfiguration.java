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
package de.ebf.security;

import de.ebf.security.annotations.PermissionScan.InitializationStrategy;
import de.ebf.security.init.DefaultPermissionInitializer;
import de.ebf.security.init.PermissionInitializer;
import de.ebf.security.repository.InMemoryPermissionModelRepository;
import de.ebf.security.repository.PermissionModelRepository;
import de.ebf.security.scanner.PermissionScanner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.Set;

@Slf4j
public class PermissionInitializerConfiguration implements ImportAware, InitializingBean {

    private AnnotationAttributes annotationAttributes;

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(annotationAttributes, "PermissionScan annotation needs to be present");
    }

    @Override
    public void setImportMetadata(@NonNull AnnotationMetadata importMetadata) {
        annotationAttributes = PermissionScanSelector.getAnnotationAttributes(importMetadata);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(PermissionInitializer.class)
    public PermissionInitializer applicationRunnerPermissionInitializer(
            ObjectProvider<PermissionModelRepository> permissionModelRepository
    ) {
        return new DefaultPermissionInitializer(permissionModelRepository.getIfAvailable(() -> {
            log.info("No PermissionModelRepository Bean found, falling back to default in-memory implementation...");
            return new InMemoryPermissionModelRepository();
        }));
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnBean(PermissionInitializer.class)
    public PermissionInitializerRunner permissionInitializerRunner(PermissionScanner permissionScanner,
                                                                   PermissionInitializer permissionInitializer) {
        return new PermissionInitializerRunner(
                permissionScanner,
                permissionInitializer,
                annotationAttributes.getEnum("strategy")
        );
    }

    @Slf4j
    @RequiredArgsConstructor
    static class PermissionInitializerRunner implements InitializingBean, ApplicationListener<ApplicationEvent> {
        private final PermissionScanner scanner;
        private final PermissionInitializer initializer;
        private final InitializationStrategy strategy;

        @Override
        public void afterPropertiesSet() {
            if (InitializationStrategy.EARLY == strategy) {
                initialize();
            }
        }

        @Override
        public void onApplicationEvent(@NonNull ApplicationEvent event) {
            if (InitializationStrategy.ON_REFRESH == strategy && event instanceof ContextRefreshedEvent) {
                initialize();
            }

            if (InitializationStrategy.ON_READY == strategy && event instanceof ApplicationReadyEvent) {
                initialize();
            }
        }

        private void initialize() {
            try {
                final Set<String> permissions = scanner.scan();

                log.debug("Running permission initializer for {}", permissions);

                initializer.initialize(permissions);
            } catch (Exception e) {
                throw new ApplicationContextException("Failed to initialize permissions", e);
            }
        }
    }

}
