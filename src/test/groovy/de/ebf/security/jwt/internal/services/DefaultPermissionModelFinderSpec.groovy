package de.ebf.security.jwt.internal.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification
import de.ebf.security.jwt.testapp.TestApplication
import de.ebf.security.jwt.testapp.models.Model


@ContextConfiguration(classes = TestApplication)
class DefaultPermissionModelFinderSpec extends Specification {

    @Autowired
    private PermissionModelFinder permissionModelFinder

    def "should return permission model definition based on Model class"(){
        when:
        def permissionModelDefinition = permissionModelFinder.find()

        then:
        permissionModelDefinition.permissionModelClass == Model
        permissionModelDefinition.defaultConstructor == Model.class.getDeclaredConstructor()
        permissionModelDefinition.permissionNameField == Model.class.getDeclaredField("name")
    }
}
