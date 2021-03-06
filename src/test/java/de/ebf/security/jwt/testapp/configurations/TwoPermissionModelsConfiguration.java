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
package de.ebf.security.jwt.testapp.configurations;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import de.ebf.security.PermissionsConfig;
import de.ebf.security.jwt.testapp.models.Model;
import de.ebf.security.jwt.testapp.othermodels.PermissionModelWithoutPermissionNameField;

@Configuration
@EnableAutoConfiguration
@PropertySource(value = { "classpath:init-permissions-disabled.properties" })
@Import(PermissionsConfig.class)
@EntityScan(basePackageClasses = { Model.class, PermissionModelWithoutPermissionNameField.class })
public class TwoPermissionModelsConfiguration {

}
