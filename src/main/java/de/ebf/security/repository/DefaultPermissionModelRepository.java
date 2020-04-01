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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * @author <a href="mailto:vuk.ljubicic@ebf.com">Vuk Ljubicic</a>
 * @since 26.03.20, Thu
 **/
@Transactional
public class DefaultPermissionModelRepository implements PermissionModelRepository {

    private EntityManager entityManager;
    private PermissionModelDefinition permissionModelDefinition;

    public DefaultPermissionModelRepository(EntityManager entityManager, PermissionModelDefinition permissionModelDefinition) {
        this.entityManager = entityManager;
        this.permissionModelDefinition = permissionModelDefinition;
    }

    @Override
    public List<Object> findAllPermissionModels() {
        CriteriaQuery selectAll = entityManager.getCriteriaBuilder()
                .createQuery(permissionModelDefinition.getPermissionModelClass());
        selectAll.select(selectAll.from(permissionModelDefinition.getPermissionModelClass()));
        return entityManager.createQuery(selectAll).getResultList();
    }

    @Override
    public void saveAllPermissionModels(List<Object> permissionModels) {
        permissionModels.forEach(pm -> entityManager.merge(pm));
    }
}
