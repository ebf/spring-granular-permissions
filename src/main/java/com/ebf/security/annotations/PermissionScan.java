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
package com.ebf.security.annotations;

import com.ebf.security.PermissionScanSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(PermissionScanSelector.class)
public @interface PermissionScan {

    /**
     * Base package names to scan for {@link Permission} annotations.
     * <p>
     * Use {@link #basePackageClasses()} for a type-safe alternative to String-based
     * package names.
     *
     * @return the array of 'basePackageNames'.
     */
    String[] basePackageNames() default {};

    /**
     * Type-safe alternative to {@link #basePackageNames()} for specifying the packages to
     * scan for {@link Permission} annotations. The package of each class specified will be scanned.
     *
     * @return the array of 'basePackageClasses'.
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Define what is the {@link InitializationStrategy} to be used when permission are scanned
     * within the classpath. Defaults to {@link InitializationStrategy#ON_READY}.
     *
     * @return initialization strategy.
     */
    InitializationStrategy strategy() default InitializationStrategy.ON_READY;

    enum InitializationStrategy {

        /**
         * Strategy that would run the {@link com.ebf.security.init.PermissionInitializer} implementation
         * as soon as the Spring Bean is ready.
         */
        EARLY,

        /**
         * Strategy that would run the {@link com.ebf.security.init.PermissionInitializer} implementation
         * when the {@link org.springframework.boot.context.event.ApplicationReadyEvent} is fired by the
         * {@link org.springframework.context.ApplicationContext}.
         */
        ON_READY,

        /**
         * Strategy that would run the {@link com.ebf.security.init.PermissionInitializer} implementation
         * when the {@link org.springframework.context.event.ContextRefreshedEvent} is fired by the
         * {@link org.springframework.context.ApplicationContext}.
         * <p>
         * If the consuming application is using Spring Cloud dependencies that are creating child
         * {@link org.springframework.context.ApplicationContext contexts} this event may be fired
         * multiple times for each child {@link org.springframework.context.ApplicationContext}.
         * This is usually when Feign Client context is created.
         */
        ON_REFRESH,

        /**
         * Strategy used when no permission initialization should occur. Be careful when using this
         * strategy as not only the {@link com.ebf.security.init.PermissionInitializer} would not be
         * executed, but also no {@link com.ebf.security.scanner.PermissionScanner} beans would be defined.
         */
        NONE
    }

}
