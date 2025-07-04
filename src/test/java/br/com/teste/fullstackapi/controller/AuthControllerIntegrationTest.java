package br.com.teste.fullstackapi.controller;

import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.teste.fullstackapi.repository.UserRepository;
import br.com.teste.fullstackapi.security.dto.RegisterRequest;

@SpringBootTest 
@AutoConfigureMockMvc 
@Transactional 
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; 

    @Autowired
    private ObjectMapper objectMapper; 

    @Autowired
    private UserRepository userRepository;

    @SuppressWarnings("unused") 
    @BeforeEach
    void setUp() {
       userRepository.deleteAll();
    }

    @Test
    void whenRegister_withValidData_shouldReturnToken() throws Exception {
       
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNome("Integration Test User");
        registerRequest.setEmail("integration.test@email.com");
        registerRequest.setSenha("password123");

        
        mockMvc.perform(post("/api/auth/register") 
                        .contentType(MediaType.APPLICATION_JSON) 
                        .content(objectMapper.writeValueAsString(registerRequest))) 
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.token", notNullValue())); 
    }
}