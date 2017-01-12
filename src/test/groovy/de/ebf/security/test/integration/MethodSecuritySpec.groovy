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



import org.apache.http.client.fluent.Request
import org.springframework.boot.SpringApplication

import spock.lang.Specification
import de.ebf.security.jwt.testapp.TestApplication
import de.ebf.security.jwt.testapp.TestApplicationWithAuthorizedUser

class MethodSecuritySpec extends Specification{

    def "http request to / should result in 403" () {

        setup:
        def app = SpringApplication.run(TestApplication)
        def encoder = Base64.encoder
        def authBytes = encoder.encode("test:test".bytes)
        def authString = new String(authBytes);

        println authString

        when:
        def returnResponse = Request.Get("http://localhost:3001/").addHeader("Authorization", "Basic $authString").execute().returnResponse()
        then:
        returnResponse.statusLine.statusCode == 403

        cleanup:
        app.stop()
        app.close()
    }

    def "http request to / should result in 200 when the user has permission" () {

        setup:
        def app = SpringApplication.run(TestApplicationWithAuthorizedUser)
        def encoder = Base64.encoder
        def authBytes = encoder.encode("user:user".bytes)
        def authString = new String(authBytes);

        println authString

        when:
        def returnResponse = Request.Get("http://localhost:3001/").addHeader("Authorization", "Basic $authString").execute().returnResponse()
        then:
        returnResponse.statusLine.statusCode == 200

        cleanup:
        app.stop()
        app.close()
    }
}
