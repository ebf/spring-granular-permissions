package de.ebf.security.jwt.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.SignedJWT;

import de.ebf.security.jwt.constants.AuthConstants;
import de.ebf.security.jwt.exceptions.JWTInvalidException;
import de.ebf.security.jwt.service.TokenExpiryService;

public class JWTAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JWSVerifier verifier;

    @Autowired
    private TokenExpiryService tokenExpiryService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        JWTAuthentication jwtAuthentication = (JWTAuthentication) authentication;

        SignedJWT jwt = (SignedJWT) jwtAuthentication.getJwt();

        if (!(jwt instanceof SignedJWT)) {
            throw new JWTInvalidException("JWT must be signed");
        }

        try {
            if (tokenExpiryService.isExpired(jwt)) {
                throw new JWTInvalidException(AuthConstants.EXPIRED_TOKEN);
            }

            if (!jwt.verify(verifier)) {
                throw new JWTInvalidException(AuthConstants.INVALID_TOKEN);
            }
        } catch (JOSEException e) {
            throw new JWTInvalidException(AuthConstants.INVALID_TOKEN);
        } catch (IllegalStateException e) {
            throw new JWTInvalidException(AuthConstants.INVALID_TOKEN);
        }

        jwtAuthentication.setAuthenticated(true);

        return jwtAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JWTAuthentication.class.isAssignableFrom(authentication);
    }

}
