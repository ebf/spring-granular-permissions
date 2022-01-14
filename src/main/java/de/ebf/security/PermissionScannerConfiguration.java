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

import de.ebf.security.annotations.PermissionScan;
import de.ebf.security.init.PermissionInitializer;
import de.ebf.security.scanner.DefaultPermissionScanner;
import de.ebf.security.scanner.PermissionScanner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
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

/**
 * Configuration that is imported when the {@link PermissionScan} annotation is used. It would define
 * a default implementation of the {@link PermissionScanner} bean if one is not already defined.
 * <p>
 * It would also provide a {@link PermissionInitializerRunner} that would be used to execute the
 * initialization of those scanned permissions using the defined {@link PermissionInitializer} bean.
 * If no {@link PermissionInitializer} bean is defined or if {@link PermissionScan.InitializationStrategy}
 * is set to {@link PermissionScan.InitializationStrategy#NONE}, no initialization should occur.
 */
public class PermissionScannerConfiguration implements ImportAware, InitializingBean {

    private AnnotationMetadata annotationMetadata;
    private AnnotationAttributes annotationAttributes;

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(annotationMetadata, "PermissionScan annotation needs to be present");
        annotationAttributes = PermissionScanSelector.getAnnotationAttributes(annotationMetadata);
    }

    @Override
    public void setImportMetadata(@NonNull AnnotationMetadata annotationMetadata) {
        this.annotationMetadata = annotationMetadata;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(PermissionScanner.class)
    public PermissionScanner defaultPermissionScanner() {
        final DefaultPermissionScanner scanner = new DefaultPermissionScanner();
        scanner.setBasePackageNames(PermissionScanSelector.getBasePackages(annotationMetadata, annotationAttributes));
        return scanner;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
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
        private final PermissionScan.InitializationStrategy strategy;

        @Override
        public void afterPropertiesSet() {
            if (PermissionScan.InitializationStrategy.EARLY == strategy) {
                initialize();
            }
        }

        @Override
        public void onApplicationEvent(@NonNull ApplicationEvent event) {
            if (PermissionScan.InitializationStrategy.ON_REFRESH == strategy && event instanceof ContextRefreshedEvent) {
                initialize();
            }

            if (PermissionScan.InitializationStrategy.ON_READY == strategy && event instanceof ApplicationReadyEvent) {
                initialize();
            }
        }

        private void initialize() {
            Assert.state(PermissionScan.InitializationStrategy.NONE != strategy,
                    "Attempted to initialize permissions with strategy set to NONE");

            try {
                final Set<String> permissions = scanner.scan();

                log.debug("Running permission initializer with strategy {} for {}", strategy, permissions);

                initializer.initialize(permissions);
            } catch (Exception e) {
                throw new ApplicationContextException("Failed to initialize permissions", e);
            }
        }
    }

}
