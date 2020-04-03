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
package de.ebf.security.test.internal.services

import spock.lang.Ignore
import spock.lang.Specification
import de.ebf.security.internal.data.PermissionModelDefinition
import de.ebf.security.internal.permission.InternalPermission;
import de.ebf.security.internal.services.PermissionModelOperations
import de.ebf.security.internal.services.impl.ReflectivePermissionModelOperations
import de.ebf.security.jwt.testapp.controllers.TestController
import de.ebf.security.jwt.testapp.models.Model

@Ignore
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
