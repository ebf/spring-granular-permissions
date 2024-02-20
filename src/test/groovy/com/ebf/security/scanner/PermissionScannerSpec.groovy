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
package com.ebf.security.scanner

import com.ebf.security.jwt.testapp.TestApplication
import com.ebf.security.scanner.PermissionScanner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = TestApplication)
class PermissionScannerSpec extends Specification {

    @Autowired
    private PermissionScanner permissionScanner

    def "should find permission defined on TestController"() {

        when:
        def permissions = permissionScanner.scan()

        then:
        permissions.size() == 4
        permissions.contains("test:request")
        permissions.contains("models:findAll")
        permissions.contains("test-multiple:request-1")
        permissions.contains("test-multiple:request-1")
    }
}
