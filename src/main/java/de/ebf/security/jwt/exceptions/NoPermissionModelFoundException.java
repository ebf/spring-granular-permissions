package de.ebf.security.jwt.exceptions;

public class NoPermissionModelFoundException extends Exception {

    public NoPermissionModelFoundException(ClassNotFoundException e) {
        super(e);
    }

    public NoPermissionModelFoundException() {
        super();
    }

    /**
     * 
     */
    private static final long serialVersionUID = -9191745275272816537L;

}
