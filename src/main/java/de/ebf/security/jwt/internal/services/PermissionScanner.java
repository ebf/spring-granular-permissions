package de.ebf.security.jwt.internal.services;

import java.util.Set;

import de.ebf.security.jwt.permission.InternalPermission;

public interface PermissionScanner {
    Set<InternalPermission> scan();
}
