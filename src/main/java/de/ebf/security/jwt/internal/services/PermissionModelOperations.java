package de.ebf.security.jwt.internal.services;

import de.ebf.security.jwt.internal.data.PermissionModelDefinition;
import de.ebf.security.jwt.permission.InternalPermission;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 *
 *
 */
public interface PermissionModelOperations {
    Object construct(PermissionModelDefinition permissionModelDefinition, InternalPermission permission);

    String getName(PermissionModelDefinition permissionModelDefinition, Object permissionRecord);
}
