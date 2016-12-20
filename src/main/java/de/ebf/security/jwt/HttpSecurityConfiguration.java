package de.ebf.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;

import de.ebf.security.jwt.authentication.JWTAuthenticationProvider;
import de.ebf.security.jwt.service.JWTTokenExpiryService;
import de.ebf.security.jwt.service.TokenExpiryService;

@Configuration
public class HttpSecurityConfiguration extends WebSecurityConfigurerAdapter {

    public HttpSecurityConfiguration() {
        super(true);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(jwtAuthenticationProvider());
    }

    @Bean
    public AuthenticationEntryPoint jwtEntryPoint() {
        return new JWTEntryPoint();
    }

    @Bean
    public AuthenticationProvider jwtAuthenticationProvider() {
        return new JWTAuthenticationProvider();
    }


}
