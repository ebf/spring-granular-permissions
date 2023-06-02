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
package com.ebf.security.internal.data;

import com.ebf.security.repository.PermissionModel;
import lombok.Value;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Value
class DefaultPermissionModelDefinition<T extends PermissionModel> implements PermissionModelDefinition<T> {

    Class<T> permissionModelClass;
    Constructor<T> constructor;

    @Override
    public @NonNull T instantiate(@NonNull String permission) {
        Assert.hasText(permission, () -> "Internal permission name can not be blank");

        final T model;

        try {
            model = constructor.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Permission model type '" + permissionModelClass + "' can not be "
                    + "an interface or an abstract class", e);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new IllegalStateException("Permission model type '" + permissionModelClass + "' needs to have "
                    + "a public no-args constructor", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Could not instantiate permission model type '" + permissionModelClass
                    + "' as the underlying constructor threw an exception", e);
        }

        model.setPermission(permission);

        return model;
    }
}
