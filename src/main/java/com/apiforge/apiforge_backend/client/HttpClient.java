package com.apiforge.apiforge_backend.client;

import com.apiforge.apiforge_backend.model.execution.ExecutionRequest;
import com.apiforge.apiforge_backend.model.execution.ExecutionResponse;

public interface HttpClient {
     ExecutionResponse execute (ExecutionRequest request);
}
