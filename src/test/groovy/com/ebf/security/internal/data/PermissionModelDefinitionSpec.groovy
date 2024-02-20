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
package com.ebf.security.internal.data

import com.ebf.security.exceptions.PermissionModelException
import com.ebf.security.internal.data.PermissionModelDefinition
import com.ebf.security.jwt.testapp.models.Model
import com.ebf.security.repository.PermissionModel
import spock.lang.Specification

import java.lang.reflect.InvocationTargetException

/**
 * @author : vladimir.spasic@ebf.com
 * @since : 05.01.22, Wed
 * */
class PermissionModelDefinitionSpec extends Specification {

    def "should fail to create definition as no-args constructor is found"() {
        when:
        PermissionModelDefinition.forType(NoArgsModel)

        then:
        def e = thrown(PermissionModelException)
        e.cause instanceof NoSuchMethodException
    }

    def "should create definition but fail to instantiate as it is abstract"() {
        setup:
        def definition = PermissionModelDefinition.forType(AbstractModel)

        when:
        definition.instantiate("test")

        then:
        def e = thrown(IllegalStateException)
        e.cause instanceof InstantiationException
    }

    def "should create definition but fail to instantiate due constructor exception"() {
        setup:
        def definition = PermissionModelDefinition.forType(ThrowingModel)

        when:
        definition.instantiate("test")

        then:
        def e = thrown(IllegalStateException)
        e.cause instanceof InvocationTargetException
    }

    def "should create definition and instantiate the permission model"() {
        setup:
        def definition = PermissionModelDefinition.forType(Model)

        when:
        def model = definition.instantiate("test")

        then:
        model.permission == "test"
    }

    private static abstract class AbstractModel implements PermissionModel {
        String permission
    }

    private static final class NoArgsModel extends AbstractModel {
        String permission

        NoArgsModel(String permission) {
            this.permission = permission
        }
    }

    private static final class ThrowingModel extends AbstractModel {
        String permission

        ThrowingModel() {
            throw new RuntimeException()
        }
    }

}
