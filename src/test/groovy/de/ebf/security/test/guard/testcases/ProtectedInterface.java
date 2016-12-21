package de.ebf.security.test.guard.testcases;

import de.ebf.security.annotations.Permission;
import de.ebf.security.annotations.ProtectedResource;

@ProtectedResource
public interface ProtectedInterface {

    @Permission("protectMe")
    void protectedMethod();

}
