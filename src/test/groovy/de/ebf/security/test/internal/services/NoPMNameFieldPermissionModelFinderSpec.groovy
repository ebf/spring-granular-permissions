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
package de.ebf.security.test.internal.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification
import de.ebf.security.exceptions.NoPermissionFieldNameFoundException
import de.ebf.security.internal.services.PermissionModelFinder
import de.ebf.security.jwt.testapp.configurations.NoPermissionModelNameFieldConfiguration

@ContextConfiguration(classes = NoPermissionModelNameFieldConfiguration)
class NoPMNameFieldPermissionModelFinderSpec extends Specification{

    @Autowired
    private PermissionModelFinder  permissionModelFinder


    def "should throw NoPermissionFieldNameFoundException" () {
        when:
        permissionModelFinder.find()

        then:
        thrown(NoPermissionFieldNameFoundException)
    }
}
