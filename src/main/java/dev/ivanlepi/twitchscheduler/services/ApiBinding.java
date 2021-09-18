package dev.ivanlepi.twitchscheduler.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ApiBinding {

    public static final Logger LOG = LoggerFactory.getLogger(ApiBinding.class);

    protected RestTemplate restTemplate;

    @Value("${client_id}")
    private String client_id;

    public ApiBinding(String accessToken) {
        this.restTemplate = new RestTemplate();
        if (accessToken != null) {
            this.restTemplate.getInterceptors().add(getBearerTokenInterceptor(accessToken));
        } else {
            this.restTemplate.getInterceptors().add(getNoBearerTokenInterceptor());
        }

    }

    /**
     * @param accessToken The access token we generated at the start of the application.
     * @return returns intercepted request with updated headers.
     */
    private ClientHttpRequestInterceptor getBearerTokenInterceptor(String accessToken) {
        return new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution)
                    throws IOException {
                request.getHeaders().add("Authorization", "Bearer " + accessToken);
                request.getHeaders().add("client-id", client_id);
                return execution.execute(request, bytes);
            }
        };
    }

    private ClientHttpRequestInterceptor getNoBearerTokenInterceptor() {
        return new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution)
                    throws IOException {
                throw new IllegalStateException("Cant access the API without an access token");
            }
        };
    }
}
