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

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority

import spock.lang.Specification
import de.ebf.security.guard.PermissionAccessDecisionVoter
import de.ebf.security.guard.PermissionSecurityAttribute

class PermissionAccessDecisionVoterSpec extends Specification {

    def "should abstain if if no attributes are present"() {
        setup:
        Authentication authentication = Mock()
        def voter = new PermissionAccessDecisionVoter()

        when:
        def result = voter.vote(authentication, null, [])

        then:
        result == PermissionAccessDecisionVoter.ACCESS_ABSTAIN
    }

    def "should deny access if authentication holds no authorities"() {
        setup:
        Authentication authentication = Mock()
        PermissionSecurityAttribute attribute = new PermissionSecurityAttribute("test")
        def configAttributes = [attribute]
        def voter = new PermissionAccessDecisionVoter()

        when:
        def result = voter.vote(authentication, null, configAttributes)

        then:
        result == PermissionAccessDecisionVoter.ACCESS_DENIED
    }

    def "should deny access if authentication holds insufficient authorities"() {
        setup:
        Authentication authentication = Mock()
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("whatever")
        authentication.authorities >> [authority]
        PermissionSecurityAttribute attribute = new PermissionSecurityAttribute("test")
        def configAttributes = [attribute]
        def voter = new PermissionAccessDecisionVoter()

        when:
        def result = voter.vote(authentication, null, configAttributes)

        then:
        result == PermissionAccessDecisionVoter.ACCESS_DENIED
    }

    def "should grant access exceptions if authentication holds sufficient authorities"() {
        setup:
        Authentication authentication = Mock()
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("test")
        authentication.authorities >> [authority]
        PermissionSecurityAttribute attribute = new PermissionSecurityAttribute("test")
        def configAttributes = [attribute]
        def voter = new PermissionAccessDecisionVoter()

        when:
        def result = voter.vote(authentication, null, configAttributes)

        then:
        result == PermissionAccessDecisionVoter.ACCESS_GRANTED
    }

    def "should grant access exceptions if authentication holds at least one sufficient authority"() {
        setup:
        Authentication authentication = Mock()
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("test")
        authentication.authorities >> [authority]
        PermissionSecurityAttribute attribute = new PermissionSecurityAttribute("test")
        PermissionSecurityAttribute attribute2 = new PermissionSecurityAttribute("test-2")
        def configAttributes = [attribute, attribute2]
        def voter = new PermissionAccessDecisionVoter()

        when:
        def result = voter.vote(authentication, null, configAttributes)

        then:
        result == PermissionAccessDecisionVoter.ACCESS_GRANTED
    }
}
