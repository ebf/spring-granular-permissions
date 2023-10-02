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
package com.ebf.security.guard

import com.ebf.security.guard.testcases.ProtectedClass
import com.ebf.security.guard.testcases.ProtectedInterface
import com.ebf.security.guard.testcases.PublicClass
import com.ebf.security.guard.testcases.PublicInterface

import java.lang.reflect.Method

import spock.lang.Specification

class PermissionSecurityAttributeRegistrySpec extends Specification {

    PermissionSecurityAttributeRegistry registry;

    def setup() {
        registry = new PermissionSecurityAttributeRegistry()
    }

    def "should return config attributes from the protected interface for protected interface public class" () {
        setup:
        Method method = PublicClass.class.getDeclaredMethod("protectedMethod")

        when:
        def result = registry.get(method, PublicClass)

        then:
        result[0] == "protectMe"
    }
    def "should return config attributes from the protected implementation for public interface protected class" () {
        setup:
        Method method = PublicInterface.class.getDeclaredMethod("publicMethod")

        when:
        def result = registry.get(method, ProtectedClass)

        then:
        result[0] == "protectedPublic"
    }
    def "should return null attributes for public interface public class" () {
        setup:
        Method method = PublicInterface.class.getDeclaredMethod("publicMethod")

        when:
        def result = registry.get(method, PublicClass)

        then:
        result.isEmpty()
    }
    def "should return config attributes from class for protected interface protected class" () {
        setup:
        Method method = ProtectedInterface.class.getDeclaredMethod("protectedMethod")

        when:
        def result = registry.get(method, ProtectedClass)

        then:
        result[0] == "overrideProtectMe"
    }
}
