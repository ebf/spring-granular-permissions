package de.ebf.security.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import de.ebf.security.jwt.constants.AuthConstants;

public class JWTEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(401);
        response.setHeader("WWW-Authenticate", AuthConstants.TOKEN_RETRIEVAL_ENDPOINT);
    }

}
