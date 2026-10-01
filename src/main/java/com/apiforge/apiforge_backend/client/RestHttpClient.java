package com.apiforge.apiforge_backend.client;

import com.apiforge.apiforge_backend.model.execution.ExecutionRequest;
import com.apiforge.apiforge_backend.model.execution.ExecutionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class RestHttpClient implements HttpClient{

    private final RestClient restClient;

    public RestHttpClient(RestClient restClient){
        this.restClient = restClient;
    }

    @Override
    public ExecutionResponse execute(ExecutionRequest request) {
        ExecutionResponse executionResponse;
        long start = System.nanoTime();
        long responseTimeMs;
        try {
            String url = request.url();
            UriComponentsBuilder uriBuilder= UriComponentsBuilder.fromUriString(url);
            request.queryParams().forEach(uriBuilder::queryParam);
            org.springframework.http.HttpMethod method = org.springframework.http.HttpMethod.valueOf(request.method().name());
            URI uri = uriBuilder.build().encode().toUri();
            RestClient.RequestBodySpec requestSpec  = restClient.method(method)
                    .uri(uri)
                    .headers(httpHeaders -> {
                        request.headers().forEach(httpHeaders::addAll);
                    });
            if (request.requestBody()!=null && !request.requestBody().isBlank()) {
                requestSpec.body(request.requestBody());
            }

            RestClient.ResponseSpec response = requestSpec.retrieve()
                    .onStatus(
                            status -> status.value() >= 400,
                            (request1, response1) -> {
                            });


            ResponseEntity<String> responseEntity = response.toEntity(String.class);
            responseTimeMs = (System.nanoTime() - start) / 1_000_000;
            int status = responseEntity.getStatusCode().value();
            Map<String, List<String>> headers = new HashMap<>();

            responseEntity.getHeaders().forEach(headers::put);
            String error = null;
            executionResponse = new ExecutionResponse(status,responseEntity.getBody(),responseTimeMs,headers,error);
        }
        catch (ResourceAccessException e){
            responseTimeMs = (System.nanoTime() - start) / 1_000_000;
            executionResponse = new ExecutionResponse(0,null, responseTimeMs,new HashMap<>(),e.getMessage());
        }
        return executionResponse;
    }
}
