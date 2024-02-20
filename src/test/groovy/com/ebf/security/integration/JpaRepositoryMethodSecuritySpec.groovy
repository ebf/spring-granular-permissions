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
package com.ebf.security.integration

import com.ebf.security.jwt.testapp.models.Model

import org.springframework.hateoas.server.core.TypeReferences
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import com.ebf.security.jwt.testapp.TestApplicationWithJpaRepositories

@ContextConfiguration(classes = TestApplicationWithJpaRepositories)
class JpaRepositoryMethodSecuritySpec extends SecuritySpecification {

    def "should respond with 401 when an attempt to access a protected jpa repository without authentication" () {

        when:
        def response = request("models", null, Object)

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED
    }

    def "should respond with 401 when an attempt to access a protected jpa repository with invalid credentials" () {

        when:
        def response = request("models", "unknown:unknown", Object)

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED
    }

    def "should respond with 403 when an attempt to access a protected jpa repository without the needed permissions is made" () {

        when:
        def response = request("models", "test:test", Object)

        then:
        response.statusCode == HttpStatus.FORBIDDEN
    }

    def "should respond with 200 when an attempt to access a protected jpa repository with the needed permissions is made"() {

        setup:
        def type = new TypeReferences.PagedModelType<Model>() {}

        when:
        def response = request("models", "user:user", type)

        then:
        response.statusCode == HttpStatus.OK

        and:
        def model = response.body

        model.metadata.totalElements == 4
        model.content[0].timestamp == 0
        model.content[1].timestamp == 0
        model.content[2].timestamp == 0
        model.content[3].timestamp == 0
    }
}
