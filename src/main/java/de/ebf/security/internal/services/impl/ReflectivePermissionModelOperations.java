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
