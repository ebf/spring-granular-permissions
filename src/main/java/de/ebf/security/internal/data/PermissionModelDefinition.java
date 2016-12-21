package de.ebf.security.internal.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 *
 *
 */
public interface PermissionModelDefinition {

    Class<?> getPermissionModelClass();

    Field getPermissionNameField();

    Constructor getDefaultConstructor();

}
