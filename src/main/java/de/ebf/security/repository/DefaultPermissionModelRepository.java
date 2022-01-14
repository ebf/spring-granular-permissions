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
package de.ebf.security.repository;

import de.ebf.security.internal.data.PermissionModelDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:vuk.ljubicic@ebf.com">Vuk Ljubicic</a>
 * @since 26.03.20, Thu
 **/
@Transactional
@RequiredArgsConstructor
public class DefaultPermissionModelRepository implements PermissionModelRepository {

    private final EntityManagerFactory entityManagerFactory;
    private final PermissionModelDefinition<PermissionModel> permissionModelDefinition;

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends PermissionModel> Collection<T> findAll() {
        final EntityManager entityManager = obtainTransactionalEntityManager(false);

        final CriteriaQuery<PermissionModel> query = entityManager.getCriteriaBuilder()
                .createQuery(permissionModelDefinition.getPermissionModelClass());
        query.select(query.from(permissionModelDefinition.getPermissionModelClass()));

        return (List<T>) entityManager.createQuery(query)
                .getResultList();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends PermissionModel> T save(@NonNull String permission) {
        final T permissionModel = (T) permissionModelDefinition.instantiate(permission);
        return save(permissionModel);
    }

    @NonNull
    @Override
    public <T extends PermissionModel> T save(@NonNull T permission) {
        final EntityManager entityManager = obtainTransactionalEntityManager(true);
        return entityManager.merge(permission);
    }

    @Override
    public <T extends PermissionModel> void delete(@NonNull T permission) {
        final EntityManager entityManager = obtainTransactionalEntityManager(true);
        entityManager.remove(permission);
    }

    private @NonNull EntityManager obtainTransactionalEntityManager(boolean required) {
        EntityManager entityManager = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);

        if (entityManager == null && !required) {
            entityManager = entityManagerFactory.createEntityManager();
        }

        if (entityManager == null) {
            throw new IllegalStateException("Could not create Entity Manager");
        }

        return entityManager;
    }
}
