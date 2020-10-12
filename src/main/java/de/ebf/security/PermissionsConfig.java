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
package de.ebf.security;

import de.ebf.security.annotations.PermissionModel;
import de.ebf.security.annotations.ProtectedResource;
import de.ebf.security.exceptions.MoreThanOnePermissionModelFoundException;
import de.ebf.security.exceptions.MoreThanOnePermissionNameFieldFoundException;
import de.ebf.security.exceptions.NoPermissionFieldNameFoundException;
import de.ebf.security.exceptions.NoPermissionModelFoundException;
import de.ebf.security.init.InitPermissions;
import de.ebf.security.internal.data.PermissionModelDefinition;
import de.ebf.security.internal.services.PermissionModelFinder;
import de.ebf.security.internal.services.PermissionModelOperations;
import de.ebf.security.internal.services.impl.DefaultPermissionModelFinder;
import de.ebf.security.internal.services.impl.InterfaceBeanScanner;
import de.ebf.security.internal.services.impl.ReflectivePermissionModelOperations;
import de.ebf.security.repository.DefaultPermissionModelRepository;
import de.ebf.security.repository.PermissionModelRepository;
import de.ebf.security.scanner.DefaultPermissionScanner;
import de.ebf.security.scanner.PermissionScanner;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.persistence.EntityManager;

@Configuration
@Import({MethodSecurityConfiguration.class})
public class PermissionsConfig {

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
    public PermissionModelDefinition permissionModelDefinition(@Autowired PermissionModelFinder permissionModelFinder) {
        try {
            return permissionModelFinder.find();
        } catch (NoPermissionModelFoundException e) {
            throw new FatalBeanException("Could not create Permission Model Definition bean. " +
                    "You need to define at least class with a @PermissionModel annotation", e);
        } catch (MoreThanOnePermissionModelFoundException e) {
            throw new FatalBeanException("Could not create Permission Model Definition bean. " +
                    "More than one Permission Model was found in classpath: " + e.getClassNames(), e);
        } catch (NoPermissionFieldNameFoundException e) {
            throw new FatalBeanException("Could not create Permission Model Definition bean. " +
                    "You need to annotate one field on your Permission Model with @PermissionNameField", e);
        } catch (MoreThanOnePermissionNameFieldFoundException e) {
            throw new FatalBeanException("Could not create Permission Model Definition bean. " +
                    "You can not annotate more than one field on your Permission Model with @PermissionNameField", e);
        }
    }

    @Bean
    @ConditionalOnMissingBean(PermissionModelRepository.class)
    public DefaultPermissionModelRepository defaultPermissionModelRepository(
            EntityManager entityManager, PermissionModelDefinition permissionModelDefinition
    ) {
        return new DefaultPermissionModelRepository(entityManager, permissionModelDefinition);
    }

    @Bean
    @ConditionalOnProperty(name="init.permissions.disable", havingValue = "false", matchIfMissing = true)
    public InitPermissions initPermissions(PermissionModelDefinition permissionModelDefinition,
                                           PermissionModelRepository permissionModelRepository,
                                           PermissionScanner permissionScanner,
                                           PermissionModelOperations permissionModelOperations) {
        return new InitPermissions(permissionModelDefinition,
                permissionModelRepository, permissionScanner, permissionModelOperations);
    }

}
