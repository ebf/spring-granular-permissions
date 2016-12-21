package de.ebf.security.internal.services;

import de.ebf.security.internal.data.PermissionModelDefinition;
import de.ebf.security.internal.permission.InternalPermission;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 *
 *
 */
public interface PermissionModelOperations {
    Object construct(PermissionModelDefinition permissionModelDefinition, InternalPermission permission);

    String getName(PermissionModelDefinition permissionModelDefinition, Object permissionRecord);
}
