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
package com.ebf.security.repository;

import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Defines contract for reading, writing and removing permissions.
 *
 * @see PermissionModel
 * @see com.ebf.security.scanner.PermissionScanner
 * @see com.ebf.security.init.PermissionInitializer
 * @author <a href="mailto:vuk.ljubicic@ebf.com">Vuk Ljubicic</a>
 * @since 26.03.20, Thu
 **/
public interface PermissionModelRepository {

    /**
     * Retrieves all permissions models from the configured store
     *
     * @param <T> permission model generic type
     * @return Collection of permission models, can't be {@literal null}
     */
    @NonNull <T extends PermissionModel> Collection<T> findAll();

    /**
     * Creates or updates the scanned {@link com.ebf.security.annotations.Permission} values that
     * were found in the classpath by the {@link com.ebf.security.scanner.PermissionScanner}.
     *
     * @param permissions Permission values to be stored, can't be {@literal null}
     */
    @SuppressWarnings("unchecked")
    default @NonNull <T extends PermissionModel> Collection<T> saveAll(@NonNull Collection<String> permissions) {
        return permissions.stream()
                .map(permission -> (T) save(permission))
                .collect(Collectors.toSet());
    }

    /**
     * Creates or updates the scanned {@link com.ebf.security.annotations.Permission} value that
     * was found in the classpath by the {@link com.ebf.security.scanner.PermissionScanner}.
     *
     * @param permission Permission value to be stored, can't be {@literal null}
     */
    @NonNull <T extends PermissionModel> T save(@NonNull String permission);

    /**
     * Creates or updates the {@link PermissionModel} instance.
     *
     * @param permission Permission model to be stored, can't be {@literal null}
     */
    @NonNull <T extends PermissionModel> T save(@NonNull T permission);

    /**
     * Removes a collection of permission models that are no longer present in the list of available
     * {@link com.ebf.security.annotations.Permission} that was found by the
     * {@link com.ebf.security.scanner.PermissionScanner}.
     *
     * @param permissions Permission models to be removed, can't be {@literal null}
     */
    default <T extends PermissionModel> void deleteAll(@NonNull Collection<T> permissions) {
        permissions.forEach(this::delete);
    }

    /**
     * Removes the permission model that is no longer present in the list of available
     * {@link com.ebf.security.annotations.Permission} that was found by the
     * {@link com.ebf.security.scanner.PermissionScanner}.
     *
     * @param permission Permission value to be removed, can't be {@literal null}
     */
    <T extends PermissionModel> void delete(@NonNull T permission);
}
