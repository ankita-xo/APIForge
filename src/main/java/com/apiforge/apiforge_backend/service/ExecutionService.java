package com.apiforge.apiforge_backend.service;

import com.apiforge.apiforge_backend.client.HttpClient;
import com.apiforge.apiforge_backend.model.execution.ExecutionRequest;
import com.apiforge.apiforge_backend.model.execution.ExecutionResponse;
import org.springframework.stereotype.Service;

@Service
public class ExecutionService {
    private final HttpClient httpClient;

    public  ExecutionService(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public ExecutionResponse execute(ExecutionRequest request){
        return httpClient.execute(request);
    }
}
