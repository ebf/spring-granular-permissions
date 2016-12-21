package de.ebf.security.test.guard.testcases;

import de.ebf.security.annotations.Permission;
import de.ebf.security.annotations.ProtectedResource;

@ProtectedResource
public class ProtectedClass implements PublicInterface, ProtectedInterface {

    @Permission("overrideProtectMe")
    @Override
    public void protectedMethod() {
        // TODO Auto-generated method stub

    }

    @Permission("protectedPublic")
    @Override
    public void publicMethod() {
        // TODO Auto-generated method stub

    }

}
