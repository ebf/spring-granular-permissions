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
package com.ebf.security.test.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.util.Base64Utils
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Specification


/**
 * @author : vladimir.spasic@ebf.com
 * @since : 05.01.22, Wed
 * */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class SecuritySpecification extends Specification {

    @Autowired
    protected ServletWebServerApplicationContext context

    @Autowired
    protected RestTemplateBuilder builder

    protected TestRestTemplate template

    def setup() {
        template = new TestRestTemplate(builder)
    }

    protected <T> ResponseEntity<T> request(String credentials, Class<T> type) {
        request("", credentials, type)
    }

    protected <T> ResponseEntity<T> request(String path, String credentials, Class<T> type) {
        request(path, credentials, ParameterizedTypeReference.forType(type))
    }

    protected <T> ResponseEntity<T> request(String path, String credentials, ParameterizedTypeReference<T> type) {
        final def headers = new HttpHeaders()

        if (credentials != null) {
            final def authorization = Base64Utils.encodeToString(credentials.bytes)
            headers.setBasicAuth(authorization)
        }

        final var uri = UriComponentsBuilder.newInstance()
            .scheme("http")
            .host("localhost")
            .port(context.webServer.port)
            .pathSegment(path)
            .build()
            .toUri()

        final def request = RequestEntity.get(uri)
                .headers(headers)
                .build()

        template.exchange(request, type)
    }

}