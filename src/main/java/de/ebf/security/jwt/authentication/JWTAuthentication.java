package de.ebf.security.jwt.authentication;

import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;

import de.ebf.security.jwt.constants.AuthConstants;
import de.ebf.security.jwt.exceptions.JWTInvalidException;

/**
 * @author Nenad Nikolic <nenad.nikolic@ebf.de>
 *
 *
 */
public class JWTAuthentication implements Authentication {

    /**
     *
     */
    private static final long serialVersionUID = 7494860493519905798L;

    private JWT jwt;

    private boolean authenticated;

    public JWTAuthentication(JWT jwt) throws ParseException {
        this.jwt = jwt;
        this.authenticated = false;
    }

    public JWT getJwt() {
        return jwt;
    }

    public JWTClaimsSet getClaims() {
        try {
            return this.jwt.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new JWTInvalidException(e.getMessage());
        }
    }

    public String getLabel() {
        return (String) this.getClaims().getClaim(AuthConstants.CLAIMS_LABEL);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return this.getClaims().getSubject();
    }

    @Override
    public String getName() {
        return this.getClaims().getSubject();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        try {
            List<String> systemFunctionNames = this.getClaims()
                    .getStringListClaim(AuthConstants.CLAIMS_SYSTEM_FUNCTIONS);

            if (systemFunctionNames == null || systemFunctionNames.isEmpty()) {
                return Collections.EMPTY_SET;
            }

            return systemFunctionNames.stream().map((systemFunctionName) -> {
                return new SimpleGrantedAuthority(systemFunctionName);
            }).collect(Collectors.toSet());

        } catch (ParseException e) {
            throw new JWTInvalidException(e.getMessage());
        }
    }

    @Override
    public Object getDetails() {
        return this.getClaims().getClaim(AuthConstants.CLAIMS_TENANT_INFORMATION);
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    public String getGroup() {
        return (String) this.getClaims().getClaim(AuthConstants.CLAIMS_GROUP);
    }

}
