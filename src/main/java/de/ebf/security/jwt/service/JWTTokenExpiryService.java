package de.ebf.security.jwt.service;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;

public class JWTTokenExpiryService implements TokenExpiryService {

    private long ttlMillis;

    @Autowired
    public JWTTokenExpiryService(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    @Override
    public boolean isExpired(JWT jwtToken) {

        if (jwtToken == null) {
            return true;
        }

        try {
            JWTClaimsSet jwtClaimsSet = jwtToken.getJWTClaimsSet();

            Date issueTime = jwtClaimsSet.getIssueTime();

            if (issueTime == null) {
                return true;
            }

            return System.currentTimeMillis() - issueTime.getTime() >= ttlMillis;

        } catch (ParseException e) {
            return true;
        }

    }

}
