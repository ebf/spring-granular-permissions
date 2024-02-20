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
package com.ebf.security;

import com.ebf.security.annotations.Permission;
import com.ebf.security.guard.PermissionAuthorizationManager;
import io.micrometer.observation.ObservationRegistry;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.Pointcuts;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Role;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.method.AuthorizationInterceptorsOrder;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

/**
 * Configuration class that is imported as a configuration candidate by the
 * {@link com.ebf.security.annotations.PermissionScan} annotation.
 * <p>
 * Primary goal of this configuration is to set up method security interceptor around the
 * {@link Permission} annotation. Methods or classes that are using this annotation are
 * subjected to introspection by the Spring AOP {@link AuthorizationManagerBeforeMethodInterceptor}
 * that is using the {@link PermissionAuthorizationManager} to evauluate if the current
 * {@link org.springframework.security.core.Authentication} has sufficient permissions.
 *
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 * @author Vladimir Spasic <vladimir.spasic@ebf.de>
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class PermissionMethodSecurityConfiguration {

    static final String ADVISOR_BEAN_NAME = "granularPermissionAuthorizationAdvisor";
    static final String INTERCEPTOR_BEAN_NAME = "granularPermissionAuthorizationMethodInterceptor";

    @Bean(name = INTERCEPTOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    static MethodInterceptor granularPermissionAuthorizationMethodInterceptor(
            ObjectProvider<SecurityContextHolderStrategy> strategyProvider,
            ObjectProvider<AuthorizationEventPublisher> eventPublisherProvider,
            ObjectProvider<ObservationRegistry> registryProvider) {

        AuthorizationManagerBeforeMethodInterceptor interceptor = new AuthorizationManagerBeforeMethodInterceptor(
                createClassOrMethodPointcut(), PermissionAuthorizationManager.create(registryProvider)
        );

        interceptor.setOrder(AuthorizationInterceptorsOrder.FIRST.getOrder());
        strategyProvider.ifAvailable(interceptor::setSecurityContextHolderStrategy);
        eventPublisherProvider.ifAvailable(interceptor::setAuthorizationEventPublisher);
        return interceptor;
    }

    private static Pointcut createClassOrMethodPointcut() {
        return Pointcuts.union(new AnnotationMatchingPointcut(null, Permission.class, true),
                new AnnotationMatchingPointcut(Permission.class, true));
    }

    static class PermissionImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            final BeanDefinition definition = registry.getBeanDefinition(INTERCEPTOR_BEAN_NAME);

            if (definition instanceof RootBeanDefinition advisor) {
                advisor.setTargetType(Advisor.class);
                registry.registerBeanDefinition(ADVISOR_BEAN_NAME, advisor);
            }
        }
    }

}
