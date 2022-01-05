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

import org.springframework.lang.NonNull;

import java.util.Collection;

/**
 * Defines contract for reading, writing and removing permissions.
 *
 * @see PermissionModel
 * @see de.ebf.security.scanner.PermissionScanner
 * @see de.ebf.security.init.PermissionInitializer
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
     * Creates the scanned {@link de.ebf.security.annotations.Permission} value that
     * was found in the classpath by the {@link de.ebf.security.scanner.PermissionScanner}.
     *
     * @param permission Permission value to be stored, can't be {@literal null}
     */
    @NonNull <T extends PermissionModel> T create(@NonNull String permission);

    /**
     * Removes the permission value that is no longer present in the list of available
     * {@link de.ebf.security.annotations.Permission} that was found by the
     * {@link de.ebf.security.scanner.PermissionScanner}.
     *
     * @param permission Permission value to be removed, can't be {@literal null}
     */
    void delete(@NonNull String permission);
}
