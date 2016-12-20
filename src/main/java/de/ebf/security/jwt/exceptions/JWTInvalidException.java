package de.ebf.security.jwt.exceptions;

import org.springframework.security.core.AuthenticationException;

public class JWTInvalidException extends AuthenticationException {

    public JWTInvalidException(String msg) {
        super(msg);
    }

    /**
     *
     */
    private static final long serialVersionUID = 4841341233914386097L;

}
