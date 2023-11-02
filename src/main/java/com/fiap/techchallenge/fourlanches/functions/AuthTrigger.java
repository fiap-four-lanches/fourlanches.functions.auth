package com.fiap.techchallenge.fourlanches.functions;

import java.util.*;
import java.util.function.Function;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthTrigger {
    @Autowired
    public Function<String, String> echo;

    @Autowired
    private CustomerRepository customerRepository;

    @FunctionName("cpf-auth")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("cpf");
        Optional<Customer> customer = customerRepository.findByDocument(query);

        if (customer.isPresent()) {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + customer.get().getEmail()).build();
        } else {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        }
    }
}
