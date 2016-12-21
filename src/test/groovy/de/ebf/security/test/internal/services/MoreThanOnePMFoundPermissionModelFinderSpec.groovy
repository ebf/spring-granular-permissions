package de.ebf.security.test.internal.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification
import de.ebf.security.exceptions.MoreThanOnePermissionModelFoundException
import de.ebf.security.internal.services.PermissionModelFinder
import de.ebf.security.jwt.testapp.configurations.TwoPermissionModelsConfiguration

@ContextConfiguration(classes = TwoPermissionModelsConfiguration)
class MoreThanOnePMFoundPermissionModelFinderSpec extends Specification {

    @Autowired
    private PermissionModelFinder permissionModelFinder

    def "should throw MoreThanOnePermissionModelFoundException" ()  {
        when:
        permissionModelFinder.find()

        then:
        thrown(MoreThanOnePermissionModelFoundException)
    }
}
