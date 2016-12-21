package de.ebf.security.test.guard

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority

import spock.lang.Specification
import de.ebf.security.guard.PermissionAccessDecisionManager
import de.ebf.security.guard.PermissionSecurityAttribute

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
