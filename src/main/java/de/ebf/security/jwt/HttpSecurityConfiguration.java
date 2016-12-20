package de.ebf.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;

import de.ebf.security.jwt.service.JWTTokenExpiryService;
import de.ebf.security.jwt.service.TokenExpiryService;

@Configuration
public class HttpSecurityConfiguration {

    @Bean
    public TokenExpiryService tokenExpiryService(@Value("${token.ttl}") long tokenTtl) {
        return new JWTTokenExpiryService(tokenTtl);
    }

    @Bean
    public JWSSigner signer(@Value("${token.secret}") String tokenSecret) throws KeyLengthException {
        return new MACSigner(tokenSecret);
    }

    @Bean
    public JWSVerifier verifier(@Value("${token.secret}") String tokenSecret) throws JOSEException {
        return new MACVerifier(tokenSecret);
    }


}
