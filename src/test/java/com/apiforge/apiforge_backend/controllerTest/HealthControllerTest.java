package com.apiforge.apiforge_backend.controllerTest;

import com.apiforge.apiforge_backend.Controller.HealthController;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
public class HealthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void healthTest() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());

    }
}
