package de.ebf.security.scanner;

import java.util.Set;

import de.ebf.security.internal.permission.InternalPermission;

public interface PermissionScanner {
    Set<InternalPermission> scan();
}
