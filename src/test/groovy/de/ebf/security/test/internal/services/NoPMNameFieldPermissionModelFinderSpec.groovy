package de.ebf.security.test.internal.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification
import de.ebf.security.exceptions.NoPermissionFieldNameFoundException
import de.ebf.security.internal.services.PermissionModelFinder
import de.ebf.security.jwt.testapp.configurations.NoPermissionModelNameFieldConfiguration

@ContextConfiguration(classes = NoPermissionModelNameFieldConfiguration)
class NoPMNameFieldPermissionModelFinderSpec extends Specification{

    @Autowired
    private PermissionModelFinder  permissionModelFinder


    def "should throw NoPermissionFieldNameFoundException" () {
        when:
        permissionModelFinder.find()

        then:
        thrown(NoPermissionFieldNameFoundException)
    }
}
