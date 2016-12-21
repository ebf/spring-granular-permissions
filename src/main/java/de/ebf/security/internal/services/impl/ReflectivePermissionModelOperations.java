package de.ebf.security.internal.services.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ebf.security.internal.data.PermissionModelDefinition;
import de.ebf.security.internal.permission.InternalPermission;
import de.ebf.security.internal.services.PermissionModelOperations;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 *
 *
 */
public class ReflectivePermissionModelOperations implements PermissionModelOperations {

    private static final Logger logger = LoggerFactory.getLogger(ReflectivePermissionModelOperations.class);

    @Override
    public Object construct(PermissionModelDefinition permissionModelDefinition, InternalPermission permission) {

        Constructor defaultConstructor = permissionModelDefinition.getDefaultConstructor();

        try {
            Object permissionModelInstance = defaultConstructor.newInstance();

            if (permissionModelDefinition.getPermissionNameField().isAccessible()) {
                permissionModelDefinition.getPermissionNameField().set(permissionModelInstance, permission.getName());
            } else {
                permissionModelDefinition.getPermissionNameField().setAccessible(true);
                permissionModelDefinition.getPermissionNameField().set(permissionModelInstance, permission.getName());
                permissionModelDefinition.getPermissionNameField().setAccessible(false);
            }

            return permissionModelInstance;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            logger.error("Error while instantiating {} through default constructor", e);
            return null;
        }
    }

    @Override
    public String getName(PermissionModelDefinition permissionModelDefinition, Object permissionRecord) {

        try {
            Object permissionNameObject;
            if (permissionModelDefinition.getPermissionNameField().isAccessible()) {
                permissionNameObject = permissionModelDefinition.getPermissionNameField().get(permissionRecord);
            } else {
                permissionModelDefinition.getPermissionNameField().setAccessible(true);
                permissionNameObject = permissionModelDefinition.getPermissionNameField().get(permissionRecord);
                permissionModelDefinition.getPermissionNameField().setAccessible(false);
            }

            return String.class.cast(permissionNameObject);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            logger.error("Error while retrieving permission name from permission record", e);
            return null;
        }
    }

}
