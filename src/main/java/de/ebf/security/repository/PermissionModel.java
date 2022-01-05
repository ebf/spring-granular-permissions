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
package de.ebf.security.repository;

import org.springframework.lang.Nullable;

/**
 * Interface that needs to be implemented by the JPA Entity class in order to
 * store and retrieve permissions by the {@link PermissionModelRepository}
 *
 * @author : vladimir.spasic@ebf.com
 * @since : 04.01.22, Tue
 **/
public interface PermissionModel {

    /**
     * Set the permission value to the implementing model
     *
     * @param permission permission name, can be {@literal null}
     */
    void setPermission(@Nullable String permission);

    /**
     * Retrieve the permission name associated with this model.
     *
     * @return permission name, or {@literal null} if none is set
     */
    @Nullable String getPermission();

}
