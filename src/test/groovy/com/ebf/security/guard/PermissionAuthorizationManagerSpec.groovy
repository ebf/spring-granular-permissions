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
package com.ebf.security.guard

import com.ebf.security.annotations.Permission
import com.ebf.security.guard.PermissionAuthorizationManager
import org.aopalliance.intercept.MethodInvocation
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils

import spock.lang.Specification

class PermissionAuthorizationManagerSpec extends Specification {

    def manager = PermissionAuthorizationManager.create()

    def authentication = Mock(Authentication)

    def invocation = Mock(MethodInvocation)

    def "should abstain if if no permissions are present"() {
        setup:
        scenario("allowed")

        when:
        def result = manager.check(() -> authentication, invocation)

        then:
        result == null
    }

    def "should deny access if authentication holds no authorities"() {
        setup:
        scenario("user")

        when:
        def result = manager.check(() -> authentication, invocation)

        then:
        !result.granted
    }

    def "should deny access if authentication holds insufficient authorities"() {
        setup:
        authentication.authorities >> AuthorityUtils.commaSeparatedStringToAuthorityList("user")
        scenario("admin")

        when:
        def result = manager.check(() -> authentication, invocation)

        then:
        !result.granted
    }

    def "should grant access if authentication holds sufficient authorities"() {
        setup:
        authentication.authorities >> AuthorityUtils.commaSeparatedStringToAuthorityList("user")
        scenario("user")

        when:
        def result = manager.check(() -> authentication, invocation)

        then:
        result.granted
    }

    def "should grant access if authentication holds at least one sufficient authority"() {
        setup:
        authentication.authorities >> AuthorityUtils.commaSeparatedStringToAuthorityList("user")
        scenario("composite")

        when:
        def result = manager.check(() -> authentication, invocation)

        then:
        result.granted
    }

    void scenario(String scenario) {
        invocation.method >> Guarded.class.getMethod(scenario)
    }

    static interface Guarded {

        @Permission("user")
        void user();

        @Permission("admin")
        void admin();

        @Permission(["admin", "user"])
        void composite();

        void allowed();

    }
}
