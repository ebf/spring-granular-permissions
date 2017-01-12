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
package de.ebf.security.internal.services;

import de.ebf.security.internal.data.PermissionModelDefinition;
import de.ebf.security.internal.permission.InternalPermission;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 *
 *
 */
public interface PermissionModelOperations {
    Object construct(PermissionModelDefinition permissionModelDefinition, InternalPermission permission);

    String getName(PermissionModelDefinition permissionModelDefinition, Object permissionRecord);
}
