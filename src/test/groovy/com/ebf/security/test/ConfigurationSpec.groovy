/*
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
package com.ebf.security.test

import com.ebf.security.annotations.PermissionScan
import com.ebf.security.init.DefaultPermissionInitializer
import com.ebf.security.init.PermissionInitializer
import com.ebf.security.internal.services.PermissionModelFinder
import com.ebf.security.internal.services.impl.DefaultPermissionModelFinder
import com.ebf.security.jwt.testapp.controllers.TestController
import com.ebf.security.jwt.testapp.models.Model
import com.ebf.security.repository.DefaultPermissionModelRepository
import com.ebf.security.repository.InMemoryPermissionModelRepository
import com.ebf.security.repository.PermissionModel
import com.ebf.security.repository.PermissionModelRepository
import com.ebf.security.scanner.DefaultPermissionScanner
import com.ebf.security.scanner.PermissionScanner
import org.assertj.core.api.InstanceOfAssertFactories
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.context.annotation.ComponentScan
import spock.lang.Specification

import static org.assertj.core.api.Assertions.assertThat

/**
 * @author : vladimir.spasic@ebf.com
 * @since : 05.01.22, Wed
 * */
class ConfigurationSpec extends Specification {

    def "should setup context with default annotation settings and JPA"() {
        setup:
        def runner = new ApplicationContextRunner()
            .withUserConfiguration(DefaultAnnotationConfiguration)

        expect:
        runner.run {
            assertThat(it)
                    .hasNotFailed()
                    .hasSingleBean(PermissionModelFinder)
                    .hasSingleBean(PermissionModelRepository)
                    .hasSingleBean(PermissionScanner)
                    .hasSingleBean(PermissionInitializer)

            assertThat(it)
                .getBean(PermissionModelFinder)
                .isInstanceOf(DefaultPermissionModelFinder)

            assertThat(it)
                .getBean(PermissionModelRepository)
                .isInstanceOf(DefaultPermissionModelRepository)
                .extracting({ it.findAll() })
                .asInstanceOf(InstanceOfAssertFactories.collection(PermissionModel.class))
                .hasSize(4)
                .extracting("permission")
                .containsExactlyInAnyOrder("models:findAll", "test:request", "test-multiple:request-1", "test-multiple:request-2")

            assertThat(it)
                    .getBean(PermissionScanner)
                    .isInstanceOf(DefaultPermissionScanner)
                    .extracting( {  it.scan() })
                    .asInstanceOf(InstanceOfAssertFactories.collection(String.class))
                    .hasSize(4)
                    .containsExactlyInAnyOrder("models:findAll", "test:request", "test-multiple:request-1", "test-multiple:request-2")

            assertThat(it)
                    .getBean(PermissionInitializer)
                    .isInstanceOf(DefaultPermissionInitializer)
        }
    }

    def "should setup context with default annotation settings with custom repository and scanner implementation"() {
        setup:
        def repo = new InMemoryPermissionModelRepository()
        repo.save("existing-permission")
        repo.save("to-be-removed-permission")

        def scanner = Mock(PermissionScanner)

        def runner = new ApplicationContextRunner()
                .withBean(PermissionScanner) { scanner }
                .withBean(PermissionModelRepository) { repo }
                .withUserConfiguration(DefaultAnnotationConfiguration)

        when:
        runner.run {
            assertThat(it)
                    .hasNotFailed()
                    .hasSingleBean(PermissionScanner)
                    .hasSingleBean(PermissionInitializer)
                    .hasSingleBean(PermissionModelRepository)
                    .hasSingleBean(PermissionModelFinder)

            assertThat(it)
                    .getBean(PermissionModelRepository)
                    .isEqualTo(repo)
                    .extracting({ it.findAll() })
                    .asInstanceOf(InstanceOfAssertFactories.collection(PermissionModel.class))
                    .hasSize(2)
                    .extracting("permission")
                    .containsExactlyInAnyOrder("existing-permission", "to-be-created-permission")

            assertThat(it)
                    .getBean(PermissionScanner)
                    .isEqualTo(scanner)
                    .extracting( {  it.scan() })
                    .asInstanceOf(InstanceOfAssertFactories.collection(String.class))
                    .hasSize(2)
                    .containsExactlyInAnyOrder("existing-permission", "to-be-created-permission")

            assertThat(it)
                    .getBean(PermissionInitializer)
                    .isInstanceOf(DefaultPermissionInitializer)
        }

        then:
        2 * scanner.scan() >> ["existing-permission", "to-be-created-permission"]
    }

    def "should setup context with custom initializer bean"() {
        setup:
        def initializer = Mock(PermissionInitializer)

        def runner = new ApplicationContextRunner()
                .withBean(PermissionInitializer) { initializer }
                .withUserConfiguration(DefaultAnnotationConfiguration)

        when:
        runner.run {
            assertThat(it)
                    .hasNotFailed()
                    .hasSingleBean(PermissionModelFinder)
                    .hasSingleBean(PermissionModelRepository)
                    .hasSingleBean(PermissionScanner)
                    .hasSingleBean(PermissionInitializer)

            assertThat(it)
                    .getBean(PermissionModelFinder)
                    .isInstanceOf(DefaultPermissionModelFinder)

            assertThat(it)
                    .getBean(PermissionInitializer)
                    .isEqualTo(initializer)

            assertThat(it)
                    .getBean(PermissionModelRepository)
                    .isInstanceOf(DefaultPermissionModelRepository)
                    .extracting({ it.findAll() })
                    .asInstanceOf(InstanceOfAssertFactories.collection(PermissionModel.class))
                    .isEmpty()

            assertThat(it)
                    .getBean(PermissionScanner)
                    .isInstanceOf(DefaultPermissionScanner)
                    .extracting( {  it.scan() })
                    .asInstanceOf(InstanceOfAssertFactories.collection(String.class))
                    .hasSize(4)
                    .containsExactlyInAnyOrder("models:findAll", "test:request", "test-multiple:request-1", "test-multiple:request-2")
        }

        then:
        1 * initializer.initialize(_)
    }

    def "should setup context without initializing permissions"() {
        setup:
        def runner = new ApplicationContextRunner()
                .withUserConfiguration(NonInitializingAnnotationConfiguration)

        expect:
        runner.run {
            assertThat(it)
                    .hasNotFailed()
                    .hasSingleBean(PermissionModelFinder)
                    .hasSingleBean(PermissionModelRepository)
                    .hasSingleBean(PermissionScanner)
                    .hasSingleBean(PermissionInitializer)

            assertThat(it)
                    .getBean(PermissionModelFinder)
                    .isInstanceOf(DefaultPermissionModelFinder)

            assertThat(it)
                    .getBean(PermissionModelRepository)
                    .isInstanceOf(DefaultPermissionModelRepository)
                    .extracting({ it.findAll() })
                    .asInstanceOf(InstanceOfAssertFactories.collection(PermissionModel.class))
                    .isEmpty()

            assertThat(it)
                    .getBean(PermissionScanner)
                    .isInstanceOf(DefaultPermissionScanner)
                    .extracting( {  it.scan() })
                    .asInstanceOf(InstanceOfAssertFactories.collection(String.class))
                    .hasSize(4)
                    .containsExactlyInAnyOrder("models:findAll", "test:request", "test-multiple:request-1", "test-multiple:request-2")
        }
    }

    @EnableAutoConfiguration
    @PermissionScan(basePackageNames = "com.ebf.security.jwt.testapp",
            strategy = PermissionScan.InitializationStrategy.EARLY)
    @EntityScan(basePackageClasses = Model.class)
    @ComponentScan(basePackageClasses = TestController.class)
    static class DefaultAnnotationConfiguration {

    }

    @EnableAutoConfiguration
    @PermissionScan(basePackageNames = "com.ebf.security.jwt.testapp",
            strategy = PermissionScan.InitializationStrategy.NONE)
    @EntityScan(basePackageClasses = Model.class)
    @ComponentScan(basePackageClasses = TestController.class)
    static class NonInitializingAnnotationConfiguration {

    }
}