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
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author : vladimir.spasic@ebf.com
 * @since : 04.01.22, Tue
 **/
public class PermissionScanSelector implements ImportSelector {

    @NonNull
    @Override
    public String[] selectImports(@NonNull AnnotationMetadata metadata) {
        final AnnotationAttributes attrs = getAnnotationAttributes(metadata);

        final List<String> classNames = new ArrayList<>();
        classNames.add(PermissionRepositoryConfiguration.class.getName());
        classNames.add(PermissionsConfig.class.getName());

        if (PermissionScan.InitializationStrategy.NONE != attrs.getEnum("strategy")) {
            classNames.add(PermissionInitializerConfiguration.class.getName());
        }

        classNames.add(PermissionMethodSecurityConfiguration.class.getName());

        return classNames.toArray(new String[0]);
    }

    static AnnotationAttributes getAnnotationAttributes(AnnotationMetadata metadata) {
        final Map<String, Object> attributes = metadata.getAnnotationAttributes(PermissionScan.class.getName());
        return AnnotationAttributes.fromMap(attributes);
    }

    static Set<String> getBasePackages(AnnotationMetadata metadata, AnnotationAttributes attributes) {
        final Set<String> basePackages = new HashSet<>();

        for (String pkg : attributes.getStringArray("basePackageNames")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : attributes.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(metadata.getClassName()));
        }
        return basePackages;
    }
}
