package de.ebf.security.repository;

import java.util.List;

/**
 * @author <a href="mailto:vuk.ljubicic@ebf.com">Vuk Ljubicic</a>
 * Defines contract for reading and writing permissions to persistent or in-memory storage
 * Default implementation supports JPA EntityManager
 * @since 26.03.20, Thu
 **/
public interface PermissionModelRepository {
    List<Object> findAllPermissionModels();

    void saveAllPermissionModels(List<Object> permissionModels);
}
