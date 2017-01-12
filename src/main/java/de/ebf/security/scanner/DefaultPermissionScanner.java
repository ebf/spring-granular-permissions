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
package de.ebf.security.scanner;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import de.ebf.security.annotations.Permission;
import de.ebf.security.internal.permission.BasicPermission;
import de.ebf.security.internal.permission.InternalPermission;
import de.ebf.security.internal.services.impl.InterfaceBeanScanner;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 *
 *
 */
public class DefaultPermissionScanner implements PermissionScanner {

    @Autowired
    private InterfaceBeanScanner protectedResourceInterfaceBeanScanner;

    private String basePackageName;

    public void setBasePackage(String basePackageName) {
        this.basePackageName = basePackageName;
    }

    @Override
    public Set<InternalPermission> scan() {
        Set<InternalPermission> systemFunctionNames = new HashSet<>();
        for (BeanDefinition bd : protectedResourceInterfaceBeanScanner.findCandidateComponents(basePackageName)) {
            try {
                final Class clazz = Class.forName(bd.getBeanClassName());
                if (clazz == null) {
                    continue;
                }
                Method[] declaredMethods = clazz.getDeclaredMethods();

                for (Method systemFunctionResource : declaredMethods) {

                    Method mostSpecificMethod = ClassUtils.getMostSpecificMethod(systemFunctionResource, clazz);

                    Permission systemFunctionNameHolder = AnnotationUtils.findAnnotation(mostSpecificMethod,
                            Permission.class);

                    if (systemFunctionNameHolder == null) {
                        continue;
                    }

                    systemFunctionNames.add(new BasicPermission(systemFunctionNameHolder.value()));
                }

            } catch (ClassNotFoundException e) {
            }
        }

        return systemFunctionNames;
    }

}
