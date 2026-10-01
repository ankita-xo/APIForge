package com.apiforge.apiforge_backend.model.execution;

import java.util.List;
import java.util.Map;

public record ExecutionRequest(HttpMethod method,
                               String url,
                               String requestBody,
                               Map<String, String> queryParams,
                               Map<String, List<String>> headers) {
}
