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
package com.ebf.security

import com.ebf.security.annotations.PermissionScan.InitializationStrategy;
import com.ebf.security.init.PermissionInitializer
import com.ebf.security.scanner.PermissionScanner
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContextException
import org.springframework.context.event.ContextRefreshedEvent
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author : vladimir.spasic@ebf.com
 * @since : 05.01.22, Wed
 * */
class PermissionInitializerRunnerSpec extends Specification {

    final def permissions = Collections.singleton("permission")

    def scanner = Mock(PermissionScanner)
    def initializer = Mock(PermissionInitializer)

    def "should catch scanner exceptions and rethrow app context exceptions"() {
        setup:
        def cause = new RuntimeException("Scanning error")
        def runner = runnerFor(InitializationStrategy.EARLY)

        when:
        runner.afterPropertiesSet()

        then:
        1 * scanner.scan() >> { throw cause }
        0 * initializer.initialize(permissions)

        and:
        def e = thrown(ApplicationContextException)
        e.cause == cause
    }

    def "should catch initializer exceptions and rethrow app context exceptions"() {
        setup:
        def cause = new RuntimeException("Initializing error")
        def runner = runnerFor(InitializationStrategy.EARLY)

        when:
        runner.afterPropertiesSet()

        then:
        1 * scanner.scan() >> permissions
        1 * initializer.initialize(permissions) >> { throw cause }

        and:
        def e = thrown(ApplicationContextException)
        e.cause == cause
    }

    def "should initialize early"() {
        setup:
        def runner = runnerFor(InitializationStrategy.EARLY)

        when:
        runner.afterPropertiesSet()

        then:
        1 * scanner.scan() >> permissions
        1 * initializer.initialize(permissions)
    }

    @Unroll("for strategy: #strategy")
    def "should not initialize early when strategy is not early"(def strategy) {
        setup:
        def runner = runnerFor(strategy)

        when:
        runner.afterPropertiesSet()

        then:
        0 * scanner.scan() >> ["permissions"]
        0 * initializer.initialize(_)

        where:
        strategy | _
        InitializationStrategy.ON_READY   | _
        InitializationStrategy.ON_REFRESH | _
        InitializationStrategy.NONE       | _
    }

    def "should initialize on application ready event"() {
        setup:
        def event = Mock(ApplicationReadyEvent)
        def runner = runnerFor(InitializationStrategy.ON_READY)

        when:
        runner.onApplicationEvent(event)

        then:
        1 * scanner.scan() >> permissions
        1 * initializer.initialize(permissions)
    }

    @Unroll("for strategy: #strategy")
    def "should not initialize on app ready event when strategy is not on-ready"(def strategy) {
        setup:
        def event = Mock(ApplicationReadyEvent)
        def runner = runnerFor(strategy)

        when:
        runner.onApplicationEvent(event)

        then:
        0 * scanner.scan() >> ["permissions"]
        0 * initializer.initialize(_)

        where:
        strategy | _
        InitializationStrategy.EARLY      | _
        InitializationStrategy.ON_REFRESH | _
        InitializationStrategy.NONE       | _
    }

    def "should initialize on context refresh event"() {
        setup:
        def event = Mock(ContextRefreshedEvent)
        def runner = runnerFor(InitializationStrategy.ON_REFRESH)

        when:
        runner.onApplicationEvent(event)

        then:
        1 * scanner.scan() >> permissions
        1 * initializer.initialize(permissions)
    }

    @Unroll("for strategy: #strategy")
    def "should not initialize on refresh event when strategy is not on-refresh"(def strategy) {
        setup:
        def event = Mock(ContextRefreshedEvent)
        def runner = runnerFor(strategy)

        when:
        runner.onApplicationEvent(event)

        then:
        0 * scanner.scan() >> ["permissions"]
        0 * initializer.initialize(_)

        where:
        strategy | _
        InitializationStrategy.EARLY    | _
        InitializationStrategy.ON_READY | _
        InitializationStrategy.NONE     | _
    }

    private def runnerFor(InitializationStrategy strategy) {
        new PermissionScannerConfiguration.PermissionInitializerRunner(
                scanner, initializer, strategy
        )
    }

}
