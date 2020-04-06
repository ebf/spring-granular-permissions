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
package de.ebf.security.test.integration

import de.ebf.security.jwt.testapp.TestApplicationWithJpaRepositories
import groovy.json.JsonSlurper
import org.apache.http.client.fluent.Request
import org.springframework.boot.SpringApplication
import spock.lang.Shared
import spock.lang.Specification

class JpaRepositoryMethodSecuritySpec extends Specification {

    @Shared
    def app

    def setupSpec() {
        app = SpringApplication.run(TestApplicationWithJpaRepositories)
    }
    def cleanupSpec() {
        app.stop()
        app.close()
    }
    def "should respond with 403 when an attempt to access a protected jpa repository without the needed permissions is made" () {
        setup:
        def encoder = Base64.encoder
        def authBytes = encoder.encode("test:test".bytes)
        def authString = new String(authBytes);

        println authString

        when:
        def returnResponse = Request.Get("http://localhost:3001/models")
                .addHeader("Authorization", "Basic $authString")
                .execute().returnResponse()
        then:
        returnResponse.statusLine.statusCode == 403
    }

    def "should respond with 200 when an attempt to access a protected jpa repository with the needed permissions is made"() {
        setup:
        def encoder = Base64.encoder
        def authBytes = encoder.encode("user:user".bytes)
        def authString = new String(authBytes);
        def jsonSlurper = new JsonSlurper()

        println authString

        when:
        def returnResponse = Request.Get("http://localhost:3001/models")
                .addHeader("Authorization", "Basic $authString")
                .execute().returnResponse()

        and:
        def json = jsonSlurper.parseText returnResponse.entity.content.text

        println json

        then:
        returnResponse.statusLine.statusCode == 200
        json.page.totalElements == 2
        json._embedded.models[0].timestamp == 0
        json._embedded.models[1].timestamp == 0
    }
}
