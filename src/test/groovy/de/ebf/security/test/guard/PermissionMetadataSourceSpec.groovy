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
package de.ebf.security.test.guard

import spock.lang.Ignore

import java.lang.reflect.Method

import spock.lang.Specification
import de.ebf.security.guard.PermissionMetadataSource
import de.ebf.security.test.guard.testcases.ProtectedClass
import de.ebf.security.test.guard.testcases.ProtectedInterface
import de.ebf.security.test.guard.testcases.PublicClass
import de.ebf.security.test.guard.testcases.PublicInterface


@Ignore
class PermissionMetadataSourceSpec extends Specification{

    def "should return config attributes from the protected interface for protected interface public class" () {
        setup:
        Method method = ProtectedInterface.class.getDeclaredMethod("protectedMethod")
        def metadataSource = new PermissionMetadataSource()

        when:
        def result = metadataSource.getAttributes(method, PublicClass)

        then:
        result[0].getAttribute() == "protectMe"
    }
    def "should return config attributes from the protected implementation for public interface protected class" () {
        setup:
        Method method = PublicInterface.class.getDeclaredMethod("publicMethod")
        def metadataSource = new PermissionMetadataSource()

        when:
        def result = metadataSource.getAttributes(method, ProtectedClass)

        then:
        result[0].getAttribute() == "protectedPublic"
    }
    def "should return null attributes for public interface public class" () {
        setup:
        Method method = PublicInterface.class.getDeclaredMethod("publicMethod")
        def metadataSource = new PermissionMetadataSource()

        when:
        def result = metadataSource.getAttributes(method, PublicClass)

        then:
        result== null
    }
    def "should return config attributes from class for protected interface protected class" () {
        setup:
        Method method = ProtectedInterface.class.getDeclaredMethod("protectedMethod")
        def metadataSource = new PermissionMetadataSource()

        when:
        def result = metadataSource.getAttributes(method, ProtectedClass)

        then:
        result[0].getAttribute() == "overrideProtectMe"
    }
}
