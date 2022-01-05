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

import lombok.Value;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implementation of the {@link PermissionModelRepository} that stores the scanned permission
 * values in memory.
 *
 * @author : vladimir.spasic@ebf.com
 * @since : 05.01.22, Wed
 **/
public class InMemoryPermissionModelRepository implements PermissionModelRepository {

    private final Set<PermissionModel> models = new LinkedHashSet<>();

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends PermissionModel> Collection<T> findAll() {
        return (Collection<T>) Collections.unmodifiableSet(models);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends PermissionModel> T create(@NonNull String permission) {
        final PermissionModel model = new SimplePermissionModel(permission);
        models.add(model);

        return (T) model;
    }

    @Override
    public void delete(@NonNull String permission) {
        models.remove(new SimplePermissionModel(permission));
    }

    @Value
    private static class SimplePermissionModel implements PermissionModel {
        String permission;

        @Override
        public void setPermission(String permission) {
            throw new UnsupportedOperationException("Can not update the permission value once it is set!");
        }

        @Override
        public String toString() {
            return "InMemoryPermissionModel(" + permission + ')';
        }
    }

}
