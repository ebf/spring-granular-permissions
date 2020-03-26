/**
 * Copyright 2009-2017 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ebf.security;

import de.ebf.security.annotations.PermissionScan;
import de.ebf.security.exceptions.MoreThanOnePermissionModelFoundException;
import de.ebf.security.exceptions.MoreThanOnePermissionNameFieldFoundException;
import de.ebf.security.exceptions.NoPermissionFieldNameFoundException;
import de.ebf.security.exceptions.NoPermissionModelFoundException;
import de.ebf.security.internal.data.PermissionModelDefinition;
import de.ebf.security.repository.DefaultPermissionModelRepository;
import de.ebf.security.repository.DefaultPermissionModelRepositoryDisable;
import de.ebf.security.repository.PermissionModelRepository;
import de.ebf.security.scanner.DefaultPermissionScanner;
import de.ebf.security.scanner.PermissionScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import de.ebf.security.annotations.PermissionModel;
import de.ebf.security.annotations.ProtectedResource;
import de.ebf.security.init.InitPermissions;
import de.ebf.security.internal.conditional.InitPermissionsDisable;
import de.ebf.security.internal.services.PermissionModelFinder;
import de.ebf.security.internal.services.PermissionModelOperations;
import de.ebf.security.internal.services.impl.DefaultPermissionModelFinder;
import de.ebf.security.internal.services.impl.InterfaceBeanScanner;
import de.ebf.security.internal.services.impl.ReflectivePermissionModelOperations;

import javax.persistence.EntityManager;

@Configuration
@Import({MethodSecurityConfiguration.class})
public class PermissionsConfig {

    private static final Logger logger = LoggerFactory.getLogger(PermissionsConfig.class);

    @Bean
    public PermissionModelFinder permissionModelFinder() {
        return new DefaultPermissionModelFinder();
    }

    @Bean
    public PermissionModelOperations permissionModelOperations() {
        return new ReflectivePermissionModelOperations();
    }

    @Bean
    public InterfaceBeanScanner permissionModelInterfaceBeanScanner() {
        InterfaceBeanScanner interfaceBeanScanner = new InterfaceBeanScanner();

        interfaceBeanScanner.addIncludeFilter(new AnnotationTypeFilter(PermissionModel.class));
        return interfaceBeanScanner;
    }

    @Bean
    public InterfaceBeanScanner protectedResourceInterfaceBeanScanner() {
        InterfaceBeanScanner interfaceBeanScanner = new InterfaceBeanScanner();

        interfaceBeanScanner.addIncludeFilter(new AnnotationTypeFilter(ProtectedResource.class));
        return interfaceBeanScanner;
    }

    @Bean
    public PermissionScanner permissionScanner() {
        return new DefaultPermissionScanner();
    }

    @Bean
    public PermissionModelDefinition permissionModelDefinition(@Autowired PermissionModelFinder permissionModelFinder) {
        try {
            return permissionModelFinder.find();
        } catch (MoreThanOnePermissionModelFoundException | NoPermissionModelFoundException
                | NoPermissionFieldNameFoundException | MoreThanOnePermissionNameFieldFoundException e) {
            logger.error("Permission model not well defined, cannot store permissions. Permission system won't work.",
                    e);
            return null;
        }
    }

    @Conditional(DefaultPermissionModelRepositoryDisable.class)
    @Bean
    public DefaultPermissionModelRepository defaultPermissionModelRepository(@Autowired EntityManager entityManager,
                                                                             @Autowired PermissionModelDefinition permissionModelDefinition) {
        return new DefaultPermissionModelRepository(entityManager, permissionModelDefinition);
    }


    @Conditional(InitPermissionsDisable.class)
    @Bean
    public InitPermissions initPermissions(@Autowired PermissionModelFinder permissionModelFinder,
                                           @Autowired PermissionModelDefinition permissionModelDefinition,
                                           @Autowired PermissionModelRepository permissionModelRepository,
                                           @Autowired PermissionScanner permissionScanner,
                                           @Autowired PermissionModelOperations permissionModelOperations) {
        return new InitPermissions(permissionModelFinder, permissionModelDefinition,
                permissionModelRepository, permissionScanner, permissionModelOperations);
    }

}
