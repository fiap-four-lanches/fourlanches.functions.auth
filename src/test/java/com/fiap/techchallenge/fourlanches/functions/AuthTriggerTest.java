package com.fiap.techchallenge.fourlanches.functions;

import com.fiap.techchallenge.fourlanches.repositories.CustomerRepository;
import com.fiap.techchallenge.fourlanches.entities.Customer;
import com.microsoft.azure.functions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    private ExecutionContext context;

    @InjectMocks
    private AuthTrigger function;

    @BeforeEach
    public void setup() {
        this.function = new AuthTrigger("fourLanchesTest" ,"123456781234567812345678" , customerRepository);

        this.context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();
    }

    @Test
    public void shouldReturnOkWhenCPFIsPassed() {
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        doReturn(Map.of("cpf", "00011122233")).when(req).getQueryParameters();

        stubCreateResponseBuilder(req);

        when(this.customerRepository.findByDocument("00011122233")).thenReturn(Optional.of(getExpectedCustomer()));

        final HttpResponseMessage ret = function.run(req, context);

        assertEquals(ret.getStatus(), HttpStatus.OK);
    }

    @Test
    public void shouldReturnNotFoundWhenThereAreNoCustomersWithGivenCPF() {
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        doReturn(Map.of("cpf", "00011122233")).when(req).getQueryParameters();

        stubCreateResponseBuilder(req);

        when(this.customerRepository.findByDocument(any())).thenReturn(Optional.empty());

        final HttpResponseMessage ret = function.run(req, context);

        assertEquals(HttpStatus.NOT_FOUND, ret.getStatus());
    }

    @Test
    public void shouldReturnAnonymousTokenWhenCPFIsEmpty() {
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        stubCreateResponseBuilder(req);

        final HttpResponseMessage ret = function.run(req, context);

        assertEquals(HttpStatus.OK, ret.getStatus());
    }

    @Test
    public void shouldReturnInternalServerErrorWhenThereIsAGenericError() {
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        doReturn(Map.of("cpf", "00011122233")).when(req).getQueryParameters();

        stubCreateResponseBuilder(req);

        when(this.customerRepository.findByDocument(null)).thenThrow(new RuntimeException());

        final HttpResponseMessage ret = function.run(req, context);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ret.getStatus());
    }

    private static void stubCreateResponseBuilder(HttpRequestMessage<Optional<String>> req) {
        doAnswer((Answer<HttpResponseMessage.Builder>) invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
        }).when(req).createResponseBuilder(any(HttpStatus.class));
    }

    private static Customer getExpectedCustomer() {
        var expectedCustomer = new Customer();
        expectedCustomer.setId(1L);
        expectedCustomer.setFirstName("John");
        expectedCustomer.setLastName("Doe");
        expectedCustomer.setEmail("john.doe@email.com");
        expectedCustomer.setDocument("00011122233");
        return expectedCustomer;
    }

}