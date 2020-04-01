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

import de.ebf.security.internal.data.PermissionModelDefinition;
import de.ebf.security.internal.permission.BasicPermission;
import de.ebf.security.internal.permission.InternalPermission;
import de.ebf.security.internal.services.PermissionModelOperations;
import de.ebf.security.repository.PermissionModelRepository;
import de.ebf.security.scanner.PermissionScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InitPermissions implements ApplicationListener<ContextRefreshedEvent>, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(InitPermissions.class);

    private PermissionModelDefinition permissionModelDefinition;
    private PermissionModelRepository permissionModelRepository;
    private PermissionScanner permissionScanner;
    private PermissionModelOperations permissionModelOperations;

    public InitPermissions(PermissionModelDefinition permissionModelDefinition,
                           PermissionModelRepository permissionModelRepository,
                           PermissionScanner permissionScanner,
                           PermissionModelOperations permissionModelOperations) {
        this.permissionModelDefinition = permissionModelDefinition;
        this.permissionModelRepository = permissionModelRepository;
        this.permissionScanner = permissionScanner;
        this.permissionModelOperations = permissionModelOperations;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        Set<InternalPermission> declaredPermissions = permissionScanner.scan();

        List<Object> existingPermissionRecords = permissionModelRepository.findAllPermissionModels();

        Set<InternalPermission> existingPermissions = existingPermissionRecords.stream()
                .map(new Function<Object, InternalPermission>() {
                    @Override
                    public InternalPermission apply(Object permissionRecord) {

                        String permissionName = permissionModelOperations.getName(permissionModelDefinition,
                                permissionRecord);

                        return new BasicPermission(permissionName);

                    }
                }).collect(Collectors.toSet());
        List<Object> permissionModelInstances = new ArrayList<>();
        declaredPermissions.forEach(declaredPermission -> {
            logger.info("Registering permission: {}", declaredPermission.getName());

            if (existingPermissions.stream().anyMatch(existingFunction ->
                    existingFunction.getName().equals(declaredPermission.getName()))) {
                logger.info("Permission {} already exists.", declaredPermission.getName());
                return;
            }

            Object permissionModelInstance =
                    permissionModelOperations.construct(permissionModelDefinition, declaredPermission);

            permissionModelInstances.add(permissionModelInstance);
        });
        permissionModelRepository.saveAllPermissionModels(permissionModelInstances);

    }

    @Override
    public int getOrder() {
        return 0;
    }

}
