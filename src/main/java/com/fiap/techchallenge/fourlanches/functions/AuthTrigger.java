package com.fiap.techchallenge.fourlanches.functions;

import com.fiap.techchallenge.fourlanches.CustomerNotFoundException;
import com.fiap.techchallenge.fourlanches.entities.Customer;
import com.fiap.techchallenge.fourlanches.repositories.CustomerRepository;
import com.fiap.techchallenge.fourlanches.token.TokenJwtUtils;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
public class AuthTrigger {

    private final CustomerRepository customerRepository;
    private final String issuer;
    private final String secret;

    public AuthTrigger(@Value("${jwt.issuer}") String issuer,
                       @Value("${jwt.secret}") String secret,
                       CustomerRepository repository) {
        this.issuer = issuer;
        this.secret = secret;
        this.customerRepository = repository;
    }

    @FunctionName("cpf-auth")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Auth Request Received");

        try {
            String cpf = request.getQueryParameters().get("cpf");

            String jwtToken = StringUtils.isEmpty(cpf) ? generateAnonymousToken() : generateCustomerToken(cpf);

            return request.createResponseBuilder(HttpStatus.OK).body(jwtToken).build();
        } catch (CustomerNotFoundException ex) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Customer not found").build();
        } catch (Exception ex) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + ex.getMessage()).build();
        }
    }

    private String generateAnonymousToken() {
        return TokenJwtUtils.generateToken(issuer, secret);
    }

    private String generateCustomerToken(String cpf) {
        Optional<Customer> optionalCustomer = customerRepository.findByDocument(cpf);
        if (optionalCustomer.isPresent()) {
            return TokenJwtUtils.generateToken(issuer, secret, optionalCustomer.get());
        } else {
            throw new CustomerNotFoundException();
        }
    }
}
