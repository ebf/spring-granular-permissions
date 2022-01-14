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
package de.ebf.security.internal.data;

import de.ebf.security.exceptions.PermissionModelException;
import de.ebf.security.repository.PermissionModel;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;

/**
 * Interface that contains all necessary data to identify an entity class and create
 * new instances out of it.
 *
 * @see de.ebf.security.repository.PermissionModel
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 */
public interface PermissionModelDefinition<T extends PermissionModel> {

    /**
     * Type of the defined permission model that implements the
     * {@link de.ebf.security.repository.PermissionModel} interface.
     *
     * @return permission model type, can't be {@literal null}
     */
    @NonNull Class<T> getPermissionModelClass();

    /**
     * Creates a new instance of the permission model. The implementing type needs to
     * have a no-args constructor in order to create a new instance.
     *
     * @return Permission model instance, can't be {@literal null}
     */
    @NonNull T instantiate(@NonNull String permission);

    /**
     * Attempt to create a {@link PermissionModelDefinition} for a given type.
     *
     * @param type Permission model type, can't be {@literal null}
     * @param <T> Permission model generic type
     * @return Permission model definition for the given type
     * @throws PermissionModelException when no available constructor is available for the give type
     */
    static <T extends PermissionModel> PermissionModelDefinition<T> forType(@NonNull Class<T> type) throws PermissionModelException {
        final Constructor<T> constructor;

        try {
            constructor = ReflectionUtils.accessibleConstructor(type);
        } catch (NoSuchMethodException e) {
            throw new PermissionModelException("Could not find a no-args accessible constructor for " +
                    "Permission Model candidate with type: " + type, e);
        }

        return new DefaultPermissionModelDefinition<>(type, constructor);
    }

}
