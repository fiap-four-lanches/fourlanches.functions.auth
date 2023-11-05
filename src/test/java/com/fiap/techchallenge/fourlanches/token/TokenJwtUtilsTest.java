package com.fiap.techchallenge.fourlanches.token;

import com.fiap.techchallenge.fourlanches.entities.Customer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TokenJwtUtilsTest {

    public static final String ISSUER = "FourLanches";
    public static final String SECRET = "123456781234567812345678";

    @Test
    public void shouldValidateGeneratedAnonymousToken() {
        String token = TokenJwtUtils.generateToken(ISSUER, SECRET, null);

        assertTrue(TokenJwtUtils.verifyToken(ISSUER, SECRET, token));
    }

    @Test
    public void shouldValidateGeneratedCustomerToken() {
        Customer customer = new Customer();
        customer.setDocument("12345678");

        String token = TokenJwtUtils.generateToken(ISSUER, SECRET, customer);

        assertTrue(TokenJwtUtils.verifyToken(ISSUER, SECRET, token));
    }

    @Test
    public void shouldValidateStticToken() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJGb3VyTGFuY2hlcyIsImV4cCI6MTY5OTE5NDgxOH0.UgkaI6GkT8c6ZJbAlK34ZmcH-FOog4gY48linAn1S_0";

        assertTrue(TokenJwtUtils.verifyToken(ISSUER, SECRET, token));
    }

}