package com.laptophub.backend.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptophub.backend.dto.AuthRequestDTO;
import com.laptophub.backend.dto.UserRegisterDTO;
import com.laptophub.backend.model.Role;
import com.laptophub.backend.model.User;
import com.laptophub.backend.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("null")
public class TestAuthHelper {

        public static class AuthInfo {
                private final String token;
                private final String userId;

                public AuthInfo(String token, String userId) {
                        this.token = token;
                        this.userId = userId;
                }

                public String getToken() {
                        return token;
                }

                public String getUserId() {
                        return userId;
                }
        }

    public static AuthInfo registerAndLogin(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String email,
            String password,
            String nombre,
            String apellido
    ) throws Exception {
        UserRegisterDTO registerDTO = UserRegisterDTO.builder()
                .email(email)
                .password(password)
                .nombre(nombre)
                .apellido(apellido)
                .build();

        MvcResult registerResult = mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String registerResponse = registerResult.getResponse().getContentAsString();
        JsonNode registerJson = objectMapper.readTree(registerResponse);
        String userId = registerJson.get("id").asText();

        String token = login(mockMvc, objectMapper, email, password);
        return new AuthInfo(token, userId);
    }

    public static String login(MockMvc mockMvc, ObjectMapper objectMapper, String email, String password) throws Exception {
        AuthRequestDTO authRequest = AuthRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("token").asText();
    }

    public static String createAdminAndLogin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            String email,
            String password
    ) throws Exception {
        User admin = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nombre("Admin")
                .apellido("User")
                .telefono("000-0000")
                .direccion("Admin Address")
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
        return login(mockMvc, objectMapper, email, password);
    }

    public static String uniqueEmail(String prefix) {
        return prefix + "." + UUID.randomUUID() + "@laptophub.com";
    }
}
