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

import com.ebf.security.jwt.testapp.TestApplication
import com.ebf.security.jwt.testapp.models.Model
import jakarta.persistence.EntityManager
import jakarta.persistence.TypedQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = TestApplication)
class InitPermissionsIntegrationSpec extends SecuritySpecification {

    @Autowired
    private EntityManager entityManager;

    def "should persist the single permission into db"() {

        setup:
        TypedQuery<Model> query = entityManager.createQuery("select m from Model m", Model);

        when:
        def models = query.getResultList()

        then:
        models.size() == 4
        models.find { it.name == "test:request" }  != null
        models.find { it.name == "models:findAll" } != null
        models.find { it.name == "test-multiple:request-1" }  != null
        models.find { it.name == "test-multiple:request-2" }  != null
    }
}
