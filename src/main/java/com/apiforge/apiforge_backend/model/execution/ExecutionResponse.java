package com.apiforge.apiforge_backend.model.execution;

import java.util.List;
import java.util.Map;

public record ExecutionResponse(int status,
                                String responseBody,
                                long responseTimeMs,
                                Map<String, List<String>> responseHeaders,
                                String error) {
}
