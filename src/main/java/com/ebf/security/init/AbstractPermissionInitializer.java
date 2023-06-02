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
package com.ebf.security.init;

import com.ebf.security.repository.PermissionModel;
import com.ebf.security.repository.PermissionModelRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base class that can be used to implement the {@link PermissionInitializer}.
 */
@RequiredArgsConstructor
public abstract class AbstractPermissionInitializer implements PermissionInitializer {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void initialize(@NonNull Set<String> declaredPermissions) {
        final Set<PermissionModel> existingPermissions = findExistingPermissions();

        log.debug("Found existing permissions from Repository {}", existingPermissions);

        final Set<String> permissionsToBeSaved = findPermissionsToBeSaved(
                declaredPermissions, existingPermissions
        );

        if (!CollectionUtils.isEmpty(permissionsToBeSaved)) {
            savePermissions(permissionsToBeSaved);
        }

        final Set<PermissionModel> permissionsToBeRemoved = findPermissionsToBeRemoved(
                declaredPermissions, existingPermissions
        );

        if (!CollectionUtils.isEmpty(permissionsToBeRemoved)) {
            removePermissions(permissionsToBeRemoved);
        }
    }

    /**
     * Method that would save the scanned permissions that are not yet stored.
     *
     * @param permissions permissions to be stored, can't be {@literal null}
     */
    protected abstract void savePermissions(@NonNull Set<String> permissions);

    /**
     * Method that would remove permissions that are no longer used.
     *
     * @param permissions permission models to be stored, can't be {@literal null}
     * @param <T> permission model type
     */
    protected abstract <T extends PermissionModel> void removePermissions(@NonNull Set<T> permissions);

    /**
     * Retrieve the list of existing permissions that are already in the {@link PermissionModelRepository}.
     *
     * @return Existing permissions, can't be {@literal null}
     */
    protected abstract @NonNull Set<PermissionModel> findExistingPermissions();

    /**
     * Method that would retrieve the list of permissions to be saved by checking if a declared permissions
     * is not already in a collection of stored {@link PermissionModel permission models}.
     *
     * @param declaredPermissions permissions currently in use, can't be {@literal null}
     * @param existingPermissions permission models that are already stored, can't be {@literal null}
     * @param <T> permission model type
     * @return permissions to be stored, can be {@literal null}
     */
    @Nullable
    protected <T extends PermissionModel> Set<String> findPermissionsToBeSaved(@NonNull Set<String> declaredPermissions,
                                                                                         @NonNull Set<T> existingPermissions) {
        return declaredPermissions.stream().filter(permission -> {
            log.debug("Checking if permission '{}' should be saved", permission);
            return shouldSavePermission(permission, existingPermissions);
        }).collect(Collectors.toSet());
    }

    /**
     * Checks if the permission that was scanned should be saved by the {@link PermissionModelRepository}.
     *
     * @param permission          Scanned permission name, can't be {@literal null}
     * @param existingPermissions Unique set of existing permission models, can't be {@literal null}
     * @return {@literal true} when the permission should be saved
     */
    protected <T extends PermissionModel> boolean shouldSavePermission(@NonNull String permission,
                                                                       @NonNull Set<T> existingPermissions) {
        return existingPermissions.stream().noneMatch(it -> permission.equals(it.getPermission()));
    }

    /**
     * Method that would retrieve the list of {@link PermissionModel permission models} to be removed by
     * checking if a declared permissions is no longer used by the application.
     *
     * @param declaredPermissions permissions currently in use, can't be {@literal null}
     * @param existingPermissions permission models that are already stored, can't be {@literal null}
     * @param <T> permission model type
     * @return permissions to be stored, can be {@literal null}
     */
    @Nullable
    protected <T extends PermissionModel> Set<T> findPermissionsToBeRemoved(@NonNull Set<String> declaredPermissions,
                                                                            @NonNull Set<T> existingPermissions) {
        return existingPermissions.stream().filter(permission -> {
            log.debug("Checking if permission '{}' should be removed", permission);
            return shouldRemovePermission(permission, declaredPermissions);
        }).collect(Collectors.toSet());
    }

    /**
     * Checks if the existing permission that is already stored in the {@link PermissionModelRepository} should be
     * removed.
     *
     * @param permission          Existing permission model, can't be {@literal null}
     * @param scannedPermissions Unique set of scanned permissions, can't be {@literal null}
     * @return {@literal true} when the permission should be removed
     */
    protected <T extends PermissionModel> boolean shouldRemovePermission(@NonNull T permission,
                                                                         @NonNull Set<String> scannedPermissions) {
        return !scannedPermissions.contains(permission.getPermission());
    }

}
