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
import de.ebf.security.internal.data.PermissionModelDefinition;
import de.ebf.security.internal.services.PermissionModelFinder;
import de.ebf.security.internal.services.impl.DefaultPermissionModelFinder;
import de.ebf.security.repository.DefaultPermissionModelRepository;
import de.ebf.security.repository.PermissionModel;
import de.ebf.security.repository.PermissionModelRepository;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

import javax.persistence.EntityManagerFactory;

/**
 * Configuration that is imported by the {@link PermissionScanSelector} when there is
 * an {@link EntityManagerFactory} present in the classpath and a Spring Bean is declared in context.
 * <p>
 * This configuration would provide an implementation of the {@link PermissionModelRepository}
 * that is storing and reading the {@link de.ebf.security.repository.PermissionModel} from the
 * persistent store using the {@link EntityManagerFactory}. This information is stored within
 * the {@link PermissionModelDefinition} and it is given to us by the {@link PermissionModelFinder}.
 *
 * @author : vladimir.spasic@ebf.com
 * @since : 05.01.22, Wed
 **/
@ConditionalOnClass(EntityManagerFactory.class)
@ConditionalOnMissingBean(PermissionModelRepository.class)
public class PermissionRepositoryConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(PermissionModelFinder.class)
    public PermissionModelFinder defaultPermissionModelFinder(EntityManagerFactory entityManagerFactory) {
        return new DefaultPermissionModelFinder(entityManagerFactory);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
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

}
