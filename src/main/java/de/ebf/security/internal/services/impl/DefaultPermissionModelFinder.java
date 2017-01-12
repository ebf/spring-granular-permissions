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
package de.ebf.security.internal.services.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.context.ApplicationContext;

import de.ebf.security.annotations.PermissionNameField;
import de.ebf.security.exceptions.MoreThanOnePermissionModelFoundException;
import de.ebf.security.exceptions.MoreThanOnePermissionNameFieldFoundException;
import de.ebf.security.exceptions.NoPermissionFieldNameFoundException;
import de.ebf.security.exceptions.NoPermissionModelFoundException;
import de.ebf.security.internal.data.DefaultPermissionModelDefinition;
import de.ebf.security.internal.data.PermissionModelDefinition;
import de.ebf.security.internal.services.PermissionModelFinder;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 *
 *
 */
public class DefaultPermissionModelFinder implements PermissionModelFinder {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private InterfaceBeanScanner permissionModelInterfaceBeanScanner;

    @Override
    public PermissionModelDefinition find()
            throws MoreThanOnePermissionModelFoundException, NoPermissionModelFoundException,
            NoPermissionFieldNameFoundException, MoreThanOnePermissionNameFieldFoundException {

        List<String> packageNames = EntityScanPackages.get(applicationContext).getPackageNames();

        Optional<Stream<BeanDefinition>> permissionModelBDs = packageNames.stream().map(packageName -> {
            return permissionModelInterfaceBeanScanner.findCandidateComponents(packageName).stream();
        }).reduce(Stream::concat);

        if (!permissionModelBDs.isPresent()) {
            throw new NoPermissionModelFoundException();
        }

        List<BeanDefinition> permissionModelBDList = permissionModelBDs.get().collect(Collectors.toList());

        if (permissionModelBDList.isEmpty()) {
            throw new NoPermissionModelFoundException();
        }

        if (permissionModelBDList.size() > 1) {
            throw new MoreThanOnePermissionModelFoundException();
        }

        BeanDefinition beanDefinition = permissionModelBDList.get(0);

        try {
            Class<?> permissionModelClass = Class.forName(beanDefinition.getBeanClassName());

            List<Field> permissionNameFields = FieldUtils.getFieldsListWithAnnotation(permissionModelClass,
                    PermissionNameField.class);

            if (permissionNameFields.isEmpty()) {
                throw new NoPermissionFieldNameFoundException();
            }

            if (permissionNameFields.size() > 1) {
                throw new MoreThanOnePermissionNameFieldFoundException();
            }

            Field permissionNameField = permissionNameFields.get(0);

            Constructor<?> defaultConstructor = ConstructorUtils.getMatchingAccessibleConstructor(permissionModelClass);

            return new DefaultPermissionModelDefinition(permissionModelClass, permissionNameField, defaultConstructor);
        } catch (ClassNotFoundException e) {
            throw new NoPermissionModelFoundException(e);
        }

    }

}
