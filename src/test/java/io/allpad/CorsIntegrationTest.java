package io.allpad;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CorsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void cors_should_allow_allpad_io() throws Exception {
        mockMvc.perform(options("/api/v1/users/me") // Use a protected endpoint to verify Security filter chain handling
                .header("Origin", "https://allpad.io")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk()) // OPTIONS should be OK even if auth is required for GET
                .andExpect(header().string("Access-Control-Allow-Origin", "https://allpad.io"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void cors_should_deny_unknown_origin() throws Exception {
        mockMvc.perform(options("/api/v1/users/me")
                .header("Origin", "https://evil.com")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }
}
