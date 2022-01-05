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

import de.ebf.security.exceptions.MoreThanOnePermissionModelFoundException;
import de.ebf.security.exceptions.NoPermissionModelFoundException;
import de.ebf.security.exceptions.PermissionModelException;
import de.ebf.security.internal.data.PermissionModelDefinition;
import de.ebf.security.internal.services.PermissionModelFinder;
import de.ebf.security.repository.PermissionModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultPermissionModelFinder implements PermissionModelFinder {

    private final EntityManagerFactory entityManagerFactory;

    @Override
    @SuppressWarnings("unchecked")
    public PermissionModelDefinition<PermissionModel> find() throws PermissionModelException {
        final Set<Class<?>> candidates = entityManagerFactory.getMetamodel()
                .getEntities()
                .stream()
                .map(EntityType::getJavaType)
                .filter(type -> ClassUtils.isAssignable(PermissionModel.class, type))
                .collect(Collectors.toSet());

        log.debug("Found following Permission Model candidates: {}", candidates);

        if (CollectionUtils.isEmpty(candidates)) {
            throw new NoPermissionModelFoundException();
        }

        if (candidates.size() > 1) {
            throw new MoreThanOnePermissionModelFoundException(candidates);
        }

        final Class<PermissionModel> candidate = (Class<PermissionModel>) CollectionUtils.firstElement(candidates);
        Assert.notNull(candidate, "Permission model candidate can not be null");

        return PermissionModelDefinition.forType(candidate);
    }

}
