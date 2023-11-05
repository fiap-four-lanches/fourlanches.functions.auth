package com.fiap.techchallenge.fourlanches.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fiap.techchallenge.fourlanches.entities.Customer;
import org.springframework.util.ObjectUtils;

import java.util.Base64;
import java.util.Date;

import static org.springframework.util.ObjectUtils.*;

public class TokenJwtUtils {

    private static final Long ADD_TIME_EXPIRATION = (1000L * 60L * 1000L);


    private TokenJwtUtils() {

    }

    public static String generateToken(String issuer, String secret) {
        return generateToken(issuer, secret, null);
    }

    public static String generateToken(String issuer, String secret, Customer customer) {
        try {
            JWTCreator.Builder builder = JWT.create()
                    .withIssuer(issuer)
                    .withExpiresAt(new Date(System.currentTimeMillis() + ADD_TIME_EXPIRATION));

            if (!isEmpty(customer)) {
                builder = builder.withClaim("cpf", customer.getDocument());
            }

            return builder.sign(getAlgorithm(secret));
        } catch (JWTCreationException exception) {
            return null;
        }
    }

    private static Algorithm getAlgorithm(String secret) {
        return Algorithm.HMAC256(Base64.getDecoder().decode(secret.getBytes()));
    }

    public static boolean verifyToken(String issuer, String secret, String token) {
        try {
            Algorithm algorithm = getAlgorithm(secret);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();

            DecodedJWT decodedJWT = verifier.verify(token);
            return issuer.equals(decodedJWT.getIssuer());
        } catch (JWTVerificationException exception){
            System.out.println(exception.getStackTrace());
        }
        return false;
    }
}