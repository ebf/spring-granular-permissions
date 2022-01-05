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

import de.ebf.security.scanner.DefaultPermissionScanner;
import de.ebf.security.scanner.PermissionScanner;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

public class PermissionsConfig implements ImportAware, InitializingBean {

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

}
