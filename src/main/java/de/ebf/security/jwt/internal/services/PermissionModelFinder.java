package de.ebf.security.jwt.internal.services;

import de.ebf.security.jwt.exceptions.MoreThanOnePermissionModelFoundException;
import de.ebf.security.jwt.exceptions.MoreThanOnePermissionNameFieldFoundException;
import de.ebf.security.jwt.exceptions.NoPermissionFieldNameFoundException;
import de.ebf.security.jwt.exceptions.NoPermissionModelFoundException;
import de.ebf.security.jwt.internal.data.PermissionModelDefinition;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 *
 *
 */
public interface PermissionModelFinder {

    PermissionModelDefinition find() throws MoreThanOnePermissionModelFoundException, NoPermissionModelFoundException,
            NoPermissionFieldNameFoundException, MoreThanOnePermissionNameFieldFoundException;

}
