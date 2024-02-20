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
package com.ebf.security.internal.services

import com.ebf.security.exceptions.MoreThanOnePermissionModelFoundException
import com.ebf.security.exceptions.NoPermissionModelFoundException
import com.ebf.security.internal.services.impl.DefaultPermissionModelFinder
import com.ebf.security.jwt.testapp.othermodels.OtherPermissionModel

import spock.lang.Specification
import com.ebf.security.jwt.testapp.models.Model

import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.metamodel.EntityType
import jakarta.persistence.metamodel.Metamodel


class DefaultPermissionModelFinderSpec extends Specification {

    def entityManagerFactory = Mock(EntityManagerFactory)
    def metamodel = Mock(Metamodel)

    def "should return permission model definition based on Model class"() {
        setup:
        def finder = new DefaultPermissionModelFinder(entityManagerFactory)

        when:
        def permissionModelDefinition = finder.find()

        then:
        1 * entityManagerFactory.metamodel >> metamodel
        1 * metamodel.entities >> [entityTypeFor(Model), entityTypeFor(String)]

        permissionModelDefinition.permissionModelClass == Model

        and:
        def model = permissionModelDefinition.instantiate("test")
        model.permission == "test"
    }

    def "should throw no model found exception"() {
        setup:
        def finder = new DefaultPermissionModelFinder(entityManagerFactory)

        when:
        finder.find()

        then:
        1 * entityManagerFactory.metamodel >> metamodel
        1 * metamodel.entities >> [entityTypeFor(String)]

        and:
        thrown(NoPermissionModelFoundException)
    }

    def "should throw more than one model found exception"() {
        setup:
        def finder = new DefaultPermissionModelFinder(entityManagerFactory)

        when:
        finder.find()

        then:
        1 * entityManagerFactory.metamodel >> metamodel
        1 * metamodel.entities >> [entityTypeFor(Model), entityTypeFor(OtherPermissionModel)]

        and:
        thrown(MoreThanOnePermissionModelFoundException)
    }

    private EntityType<?> entityTypeFor(Class<?> type) {
        def entityType = Mock(EntityType)
        entityType.javaType >> type
        return entityType
    }

}
