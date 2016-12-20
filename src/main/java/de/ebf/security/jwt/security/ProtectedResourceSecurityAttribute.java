package de.ebf.security.jwt.security;

import org.springframework.security.access.ConfigAttribute;

public class ProtectedResourceSecurityAttribute implements ConfigAttribute {

    private static final long serialVersionUID = 6648857928991476524L;
    private String attribute;

    public ProtectedResourceSecurityAttribute(String attribute) {
        super();
        this.attribute = attribute;
    }


    public String getAttribute() {
        return attribute;
    }

}
