package de.ebf.security.jwt.internal.data;

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
