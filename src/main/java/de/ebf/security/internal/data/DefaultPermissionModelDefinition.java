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
package de.ebf.security.internal.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class DefaultPermissionModelDefinition implements PermissionModelDefinition {

    private Class<?> permissionModelClass;
    private Field permissionNameField;
    private Constructor defaultConstructor;

    public DefaultPermissionModelDefinition(Class<?> permissionModelClass, Field permissionNameField,
            Constructor defaultConstructor) {
        this.permissionModelClass = permissionModelClass;
        this.permissionNameField = permissionNameField;
        this.defaultConstructor = defaultConstructor;
    }

    @Override
    public Class<?> getPermissionModelClass() {
        return permissionModelClass;
    }

    @Override
    public Field getPermissionNameField() {
        return permissionNameField;
    }

    @Override
    public Constructor getDefaultConstructor() {
        return defaultConstructor;
    }

    public void setPermissionModelClass(Class<?> permissionModelClass) {
        this.permissionModelClass = permissionModelClass;
    }

    public void setPermissionNameField(Field permissionNameField) {
        this.permissionNameField = permissionNameField;
    }

    public void setDefaultConstructor(Constructor defaultConstructor) {
        this.defaultConstructor = defaultConstructor;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((defaultConstructor == null) ? 0 : defaultConstructor.hashCode());
        result = prime * result + ((permissionModelClass == null) ? 0 : permissionModelClass.hashCode());
        result = prime * result + ((permissionNameField == null) ? 0 : permissionNameField.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefaultPermissionModelDefinition other = (DefaultPermissionModelDefinition) obj;
        if (defaultConstructor == null) {
            if (other.defaultConstructor != null)
                return false;
        } else if (!defaultConstructor.equals(other.defaultConstructor))
            return false;
        if (permissionModelClass == null) {
            if (other.permissionModelClass != null)
                return false;
        } else if (!permissionModelClass.equals(other.permissionModelClass))
            return false;
        if (permissionNameField == null) {
            if (other.permissionNameField != null)
                return false;
        } else if (!permissionNameField.equals(other.permissionNameField))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DefaultPermissionModelDefinition [permissionModelClass=" + permissionModelClass
                + ", permissionNameField=" + permissionNameField + ", defaultConstructor=" + defaultConstructor + "]";
    }

}
