package com.fiap.techchallenge.fourlanches.functions;

import com.fiap.techchallenge.fourlanches.entities.Customer;
import com.fiap.techchallenge.fourlanches.repositories.CustomerRepository;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthTrigger {

    private final CustomerRepository customerRepository;

    @Autowired
    public AuthTrigger(CustomerRepository repository) {
        this.customerRepository = repository;
    }

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
