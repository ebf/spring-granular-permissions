package de.ebf.security.repository;

import de.ebf.security.init.InitPermissions;
import de.ebf.security.internal.data.PermissionModelDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
