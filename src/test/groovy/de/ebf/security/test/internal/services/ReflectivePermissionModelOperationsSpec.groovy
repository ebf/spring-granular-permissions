package de.ebf.security.test.internal.services

import spock.lang.Specification
import de.ebf.security.internal.data.PermissionModelDefinition
import de.ebf.security.internal.permission.InternalPermission;
import de.ebf.security.internal.services.PermissionModelOperations
import de.ebf.security.internal.services.impl.ReflectivePermissionModelOperations
import de.ebf.security.jwt.testapp.controllers.TestController
import de.ebf.security.jwt.testapp.models.Model

class ReflectivePermissionModelOperationsSpec extends Specification {

    def "should return null if something went wrong during instantiation" () {
        setup:
        // setting wrong class to cause failure
        def constructor = TestController.class.getConstructor()
        PermissionModelDefinition definition = Mock()

        definition.defaultConstructor >> constructor
        definition.permissionNameField >> Model.class.getDeclaredField("name")

        InternalPermission permission = Mock()
        permission.name >> "test"

        PermissionModelOperations operations = new ReflectivePermissionModelOperations()

        when:
        def instance = operations.construct(definition, permission)

        then:
        instance == null
    }

    def "should return the created object using the default provided constructor, object.name should be set"(){

        setup:
        def constructor = Model.class.getConstructor()
        PermissionModelDefinition definition = Mock()
        definition.defaultConstructor >> constructor
        definition.permissionModelClass >> Model
        definition.permissionNameField >> Model.class.getDeclaredField("wrongTypeField")

        InternalPermission permission = Mock()
        permission.name >> "test"

        PermissionModelOperations operations = new ReflectivePermissionModelOperations()

        when:
        def instance = operations.construct(definition, permission)

        then:
        instance == null
    }

    def "should return null if something went wrong with retrieving the fields value" () {
        setup:
        def constructor = Model.class.getConstructor()
        PermissionModelDefinition definition = Mock()
        definition.defaultConstructor >> constructor
        definition.permissionModelClass >> Model
        definition.permissionNameField >> Model.class.getDeclaredField("name")

        Model model = new Model()
        model.name = "test"

        PermissionModelOperations operations = new ReflectivePermissionModelOperations()

        when:
        def string = operations.getName(definition, model)

        then:
        string instanceof String
        string == "test"
    }

    def "should reflectivly retrieve the permission name out of a passed in object" () {

        setup:
        def constructor = Model.class.getConstructor()
        PermissionModelDefinition definition = Mock()
        definition.defaultConstructor >> constructor
        definition.permissionModelClass >> Model
        definition.permissionNameField >> Model.class.getDeclaredField("name")

        Model model = new Model()
        model.name = "test"

        PermissionModelOperations operations = new ReflectivePermissionModelOperations()

        when:
        def string = operations.getName(definition, model)

        then:
        string instanceof String
        string == "test"
    }
}
