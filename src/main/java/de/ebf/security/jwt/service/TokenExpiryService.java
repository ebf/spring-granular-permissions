package de.ebf.security.jwt.service;

import com.nimbusds.jwt.JWT;

public interface TokenExpiryService {

    boolean isExpired(JWT jwtToken);
}
