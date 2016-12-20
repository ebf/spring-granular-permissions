package de.ebf.security.jwt.internal.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification
import de.ebf.security.jwt.exceptions.NoPermissionModelFoundException
import de.ebf.security.jwt.testapp.configurations.NoPermissionModelConfiguration


@ContextConfiguration(classes = NoPermissionModelConfiguration)
class NoPMFoundPermissionModelFinderSpec extends Specification {

    @Autowired
    private PermissionModelFinder permissionModelFinder

    def "should throw NoPermissionModelFoundException" () {
        when:
        permissionModelFinder.find()

        then:
        thrown(NoPermissionModelFoundException)
    }
}
