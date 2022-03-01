/**
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
package de.ebf.security.jwt.testapp.controllers;

import de.ebf.security.annotations.Permission;
import de.ebf.security.annotations.ProtectedResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ProtectedResource
public class TestController {

    @RequestMapping(path = "/")
    @Permission(value = "test:request")
    public void testRequest() {

    }

    @RequestMapping(path = "/multiple-permissions")
    @Permission(value = { "test-multiple:request-1", "test-multiple:request-2" })
    public void testMultiplePermissionsRequest() {

    }

}
