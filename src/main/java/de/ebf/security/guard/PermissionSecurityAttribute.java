package de.ebf.security.guard;

import org.springframework.security.access.ConfigAttribute;

public class PermissionSecurityAttribute implements ConfigAttribute {

    private static final long serialVersionUID = 6648857928991476524L;
    private String attribute;

    public PermissionSecurityAttribute(String attribute) {
        super();
        this.attribute = attribute;
    }


    public String getAttribute() {
        return attribute;
    }

}
