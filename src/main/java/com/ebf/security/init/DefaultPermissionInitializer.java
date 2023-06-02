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
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultPermissionInitializer extends AbstractPermissionInitializer {

    private final PermissionModelRepository permissionModelRepository;

    @Override
    @Transactional
    public void initialize(@NonNull Set<String> declaredPermissions) {
        super.initialize(declaredPermissions);
    }

    @Override
    protected void savePermissions(@NonNull Set<String> permissions) {
        final Collection<PermissionModel> models = permissionModelRepository.saveAll(permissions);

        log.info("The following Permission models have been saved: {}", models);
    }

    @Override
    protected <T extends PermissionModel> void removePermissions(@NonNull Set<T> permissions) {
        permissionModelRepository.deleteAll(permissions);

        log.info("The following Permission models have been removed: {}", permissions);
    }

    @NonNull
    @Override
    protected Set<PermissionModel> findExistingPermissions() {
        return permissionModelRepository.findAll()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

}
