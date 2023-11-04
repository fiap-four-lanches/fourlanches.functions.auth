package com.fiap.techchallenge.fourlanches.functions;

import com.fiap.techchallenge.fourlanches.repositories.CustomerRepository;
import com.fiap.techchallenge.fourlanches.entities.Customer;
import com.microsoft.azure.functions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.*;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTriggerTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AuthTrigger function;

    @BeforeEach
    public void setup() {
        this.function = new AuthTrigger(customerRepository);
    }


    @Test
    public void shouldReturnOkWhenCPFIsPassed() {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("cpf", "00011122233");
        doReturn(queryParams).when(req).getQueryParameters();

        doAnswer((Answer<HttpResponseMessage.Builder>) invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        var expectedCustomer = new Customer();
        expectedCustomer.setId(1L);
        expectedCustomer.setFirstName("John");
        expectedCustomer.setLastName("Doe");
        expectedCustomer.setEmail("john.doe@email.com");
        expectedCustomer.setDocument("00011122233");

        when(this.customerRepository.findByDocument("00011122233"))
                .thenReturn(Optional.of(expectedCustomer));

        // Invoke
        final HttpResponseMessage ret = function.run(req, context);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
    }

    @Test
    public void shouldReturnBadRequestWhenCPFIsEmpty() {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        doAnswer((Answer<HttpResponseMessage.Builder>) invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        when(this.customerRepository.findByDocument(null))
                .thenReturn(Optional.empty());

        // Invoke
        final HttpResponseMessage ret = function.run(req, context);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.BAD_REQUEST);
    }
}