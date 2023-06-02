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
package com.ebf.security.internal.services;

import com.ebf.security.exceptions.PermissionModelException;
import com.ebf.security.internal.data.PermissionModelDefinition;
import com.ebf.security.repository.PermissionModel;

/**
 * Interface used to find a {@link PermissionModelDefinition} from an entity type that
 * implements the {@link com.ebf.security.repository.PermissionModel} interface.
 *
 * @see PermissionModel
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 */
public interface PermissionModelFinder {

    /**
     * Attempts to find a {@link PermissionModelDefinition} from the current application context.
     *
     * @return permission model definition
     * @throws PermissionModelException when there is an issue when locating permission model entity type implementations
     */
    PermissionModelDefinition<PermissionModel> find() throws PermissionModelException;

}
