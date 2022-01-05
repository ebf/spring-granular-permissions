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
package de.ebf.security.init;

import de.ebf.security.repository.PermissionModel;
import de.ebf.security.repository.PermissionModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DefaultPermissionInitializer implements PermissionInitializer {

    private final PermissionModelRepository permissionModelRepository;

    @Override
    public void initialize(@NonNull Set<String> declaredPermissions) {
        final Set<String> existingPermissions = findExistingPermissions();

        log.debug("Found existing permissions from Repository {}", existingPermissions);

        declaredPermissions.forEach(permission -> {
            log.debug("Checking if permission is already stored: {}", permission);

            if (existingPermissions.contains(permission)) {
                log.info("Permission {} already exists.", permission);
                return;
            }

            final PermissionModel model = permissionModelRepository.create(permission);

            log.info("Permission model has been created for value '{}': {}", permission, model);
        });

        existingPermissions.forEach(permission -> {
            log.debug("Checking if permission should be removed: {}", permission);

            if (declaredPermissions.contains(permission)) {
                return;
            }

            log.info("Removing permission model for value: '{}'", permission);

            permissionModelRepository.delete(permission);
        });
    }

    private Set<String> findExistingPermissions() {
        return permissionModelRepository.findAll()
                .stream()
                .filter(Objects::nonNull)
                .map(PermissionModel::getPermission)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }

}
