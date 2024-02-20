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

import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import com.ebf.security.jwt.testapp.TestApplicationWithAuthorizedUser

@ContextConfiguration(classes = TestApplicationWithAuthorizedUser)
class MethodSecuritySpec extends SecuritySpecification {

    def "http request to / should result in 401 when no authentication is present" () {

        when:
        def response = request(null, Object)

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED
    }

    def "http request to / should result in 401 when invalid user credentials are sent" () {

        when:
        def response = request("unknown:unknown", Object)

        then:
        response.statusCode == HttpStatus.UNAUTHORIZED
    }

    def "http request to / should result in 200 when the user has permission" () {

        when:
        def response = request("user:user", Object)

        then:
        response.statusCode == HttpStatus.OK
    }

    def "http request to / should result in 403 when the user does not have sufficient permissions" () {

        when:
        def response = request("test:test", Object)

        then:
        response.statusCode == HttpStatus.FORBIDDEN
    }

}
