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
package de.ebf.security.test.guard

import de.ebf.security.guard.PermissionAccessDecisionManager
import de.ebf.security.guard.PermissionSecurityAttribute
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import spock.lang.Specification

class PermissionAccessDecisionManagerSpec extends Specification {

    def "should throw accessDeniedException if authentication holds no authorities"() {
        setup:
        Authentication authentication = Mock()
        PermissionSecurityAttribute attribute = Mock()
        def configAttributes = [attribute]
        def guard = new PermissionAccessDecisionManager()
        attribute.getAttribute() >> "test"

        when:
        guard.decide(authentication, null, configAttributes)

        then:
        thrown(AccessDeniedException)
    }
    def "should throw accessDeniedException if authentication holds insufficient authorities"() {
        setup:
        Authentication authentication = Mock()
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("whatever");
        authentication.authorities >> [authority]
        PermissionSecurityAttribute attribute = Mock()
        attribute.getAttribute() >> "test"
        def configAttributes = [attribute]
        def guard = new PermissionAccessDecisionManager()

        when:
        guard.decide(authentication, null, configAttributes)

        then:
        thrown(AccessDeniedException)
    }

    def "should execute without exceptions if authentication holds sufficient authorities"() {
        setup:
        Authentication authentication = Mock()
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("test");
        authentication.authorities >> [authority]
        PermissionSecurityAttribute attribute = Mock()
        attribute.getAttribute() >> "test"
        def configAttributes = [attribute]
        def guard = new PermissionAccessDecisionManager()

        when:
        guard.decide(authentication, null, configAttributes)

        then:
        notThrown(Exception)
    }
}
