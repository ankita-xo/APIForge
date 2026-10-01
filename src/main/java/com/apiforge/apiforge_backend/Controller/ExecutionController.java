package com.apiforge.apiforge_backend.Controller;

import com.apiforge.apiforge_backend.model.execution.ExecutionRequest;
import com.apiforge.apiforge_backend.model.execution.ExecutionResponse;
import com.apiforge.apiforge_backend.service.ExecutionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExecutionController {
    private final ExecutionService executionService;

    public ExecutionController(ExecutionService executionService){
        this.executionService=executionService;
    }

    @PostMapping("/executions")
    public ExecutionResponse executionResponse(@RequestBody ExecutionRequest request){
        return executionService.execute(request);
    }
}
