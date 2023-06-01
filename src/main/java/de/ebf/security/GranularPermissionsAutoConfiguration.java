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

import de.ebf.security.exceptions.MoreThanOnePermissionModelFoundException;
import de.ebf.security.exceptions.NoPermissionModelFoundException;
import de.ebf.security.exceptions.PermissionModelException;
import de.ebf.security.init.DefaultPermissionInitializer;
import de.ebf.security.init.PermissionInitializer;
import de.ebf.security.internal.data.PermissionModelDefinition;
import de.ebf.security.internal.services.PermissionModelFinder;
import de.ebf.security.internal.services.impl.DefaultPermissionModelFinder;
import de.ebf.security.repository.DefaultPermissionModelRepository;
import de.ebf.security.repository.InMemoryPermissionModelRepository;
import de.ebf.security.repository.PermissionModel;
import de.ebf.security.repository.PermissionModelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import jakarta.persistence.EntityManagerFactory;

/**
 * Spring auto-configuration class that would provide the default implementations of the
 * {@link PermissionModelRepository} that is based on the {@link EntityManagerFactory} and
 * an JPA entity that implements the {@link PermissionModel}.
 * <p>
 * The {@link PermissionModel} entity is being found using the {@link PermissionModelFinder}
 * that would provide sufficient information to the repository and {@link EntityManagerFactory}
 * on how to read, store or delete those entities.
 * <p>
 * In case a custom implementation of the {@link PermissionModelRepository} present in the context,
 * the default implementations would be ignored or when the {@link EntityManagerFactory} is not
 * present in the classpath.
 * <p>
 * Apart from setting up the {@link PermissionModelRepository}, the {@link PermissionInitializer}
 * bean is defined that would be used to persist new and delete unused permissions that were picked
 * up by the {@link de.ebf.security.scanner.PermissionScanner}. The {@link PermissionInitializer}
 * is using the {@link PermissionModelRepository} to perform those operations, therefore if no
 * repository is present, a default {@link InMemoryPermissionModelRepository} implementation is used.
 * <p>
 * You can disable the permission initialization completely by using the <i>strategy</i> attribute on
 * the {@link de.ebf.security.annotations.PermissionScan} annotation and setting it to
 * {@link de.ebf.security.annotations.PermissionScan.InitializationStrategy#NONE}.
 *
 * @author : vladimir.spasic@ebf.com
 * @since : 05.01.22, Wed
 **/
@Slf4j
@Configuration
@AutoConfigureAfter(HibernateJpaAutoConfiguration.class)
public class GranularPermissionsAutoConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnBean(EntityManagerFactory.class)
    @ConditionalOnMissingBean(PermissionModelFinder.class)
    public PermissionModelFinder defaultPermissionModelFinder(EntityManagerFactory entityManagerFactory) {
        return new DefaultPermissionModelFinder(entityManagerFactory);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnBean(EntityManagerFactory.class)
    @ConditionalOnMissingBean(PermissionModelRepository.class)
    public PermissionModelRepository defaultPermissionModelRepository(
            EntityManagerFactory entityManagerFactory,
            PermissionModelFinder permissionModelFinder
    ) {
        final PermissionModelDefinition<PermissionModel> permissionModelDefinition;

        try {
            permissionModelDefinition = permissionModelFinder.find();
        } catch (NoPermissionModelFoundException e) {
            throw new FatalBeanException("Could not create Permission Model Definition bean. " +
                    "You need to define at least class with a @PermissionModel annotation", e);
        } catch (MoreThanOnePermissionModelFoundException e) {
            throw new FatalBeanException("Could not create Permission Model Definition bean. " +
                    "More than one Permission Model was found in classpath: " + e.getClassNames(), e);
        } catch (PermissionModelException e) {
            throw new FatalBeanException("Could not create Permission Model Definition bean.", e);
        }

        return new DefaultPermissionModelRepository(entityManagerFactory, permissionModelDefinition);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(PermissionInitializer.class)
    public PermissionInitializer applicationRunnerPermissionInitializer(
            ObjectProvider<PermissionModelRepository> permissionModelRepository
    ) {
        return new DefaultPermissionInitializer(permissionModelRepository.getIfAvailable(() -> {
            log.info("No PermissionModelRepository Bean found, falling back to default in-memory implementation...");
            return new InMemoryPermissionModelRepository();
        }));
    }

}
