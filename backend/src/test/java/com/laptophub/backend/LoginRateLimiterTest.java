package com.laptophub.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptophub.backend.dto.AuthRequestDTO;
import com.laptophub.backend.support.TestAuthHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Test del rate limiting en el endpoint de login.
 * Verifica que después de 5 intentos fallidos, se bloquea el login.
 */
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("null")
public class LoginRateLimiterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String validEmail;

    @BeforeEach
    public void setup() throws Exception {
        System.out.println("\n════════════════════════════════════════════════════════════════════");
        System.out.println("🔧 SETUP: Registrando usuario de prueba para rate limiting tests");
        System.out.println("════════════════════════════════════════════════════════════════════");
        
        validEmail = TestAuthHelper.uniqueEmail("ratelimit.user");
        TestAuthHelper.registerAndLogin(
                mockMvc,
                objectMapper,
                validEmail,
                "correct_password",
                "Rate",
                "Limit"
        );
        
        System.out.println("✅ Usuario registrado: " + validEmail);
        System.out.println("✅ Contraseña correcta: correct_password");
        System.out.println("════════════════════════════════════════════════════════════════════\n");
    }

    @Test
    public void testRateLimitAfter5Attempts() throws Exception {
        System.out.println("\n╔═════════════════════════════════════════════════════════════════════╗");
        System.out.println("║  TEST 1: Rate Limiting - Bloqueo después de 5 intentos fallidos   ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════╝\n");
        
        // Los primeros 5 intentos con contraseña incorrecta deben devolver 400 (credenciales inválidas)
        System.out.println("📋 Configuración: IP simulada = 192.168.1.100");
        System.out.println("📋 Email válido: " + validEmail);
        System.out.println("📋 Límite: 5 intentos cada 15 minutos\n");
        
        for (int i = 1; i <= 5; i++) {
            System.out.println("🔄 Intento " + i + "/5 con contraseña incorrecta...");
            
            AuthRequestDTO request = AuthRequestDTO.builder()
                    .email(validEmail)
                    .password("wrong_password_" + i)
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .header("X-Forwarded-For", "192.168.1.100") // Simula una IP específica
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Credenciales invalidas"));
            
            System.out.println("   ✅ Respuesta: 400 Bad Request");
            System.out.println("   📊 Fichas restantes: " + (5 - i) + "/5");
            System.out.println("   💬 Mensaje: \"Credenciales invalidas\"\n");
        }

        // El 6to intento debe devolver 429 (Too Many Requests)
        System.out.println("🔄 Intento 6/5 (excedió el límite)...");
        
        AuthRequestDTO request = AuthRequestDTO.builder()
                .email(validEmail)
                .password("another_wrong_password")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", "192.168.1.100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value(
                    "Demasiados intentos de login fallidos. Por favor, espera 15 minutos antes de volver a intentar."
                ));
        
        System.out.println("   🚫 Respuesta: 429 Too Many Requests");
        System.out.println("   📊 Fichas restantes: 0/5 (BLOQUEADO)");
        System.out.println("   💬 Mensaje: \"Demasiados intentos de login fallidos. Por favor, espera 15 minutos...\"");
        System.out.println("\n✅ TEST 1 PASÓ: Rate limiting bloqueó correctamente después de 5 intentos\n");
    }

    @Test
    public void testDifferentIpNotAffected() throws Exception {
        System.out.println("\n╔═════════════════════════════════════════════════════════════════════╗");
        System.out.println("║  TEST 2: Rate Limiting - IPs diferentes tienen límites separados  ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("📋 Escenario: Bloquear IP1, verificar que IP2 no se afecte");
        System.out.println("📋 IP1: 192.168.1.200");
        System.out.println("📋 IP2: 192.168.1.201\n");
        
        // 5 intentos desde una IP
        System.out.println("🔄 Agotando 5 intentos desde IP1 (192.168.1.200)...\n");
        
        for (int i = 1; i <= 5; i++) {
            AuthRequestDTO request = AuthRequestDTO.builder()
                    .email(validEmail)
                    .password("wrong_password")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .header("X-Forwarded-For", "192.168.1.200")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
            
            System.out.println("   Intento " + i + "/5 desde IP1: ✅ 400 Bad Request (fichas: " + (5-i) + "/5)");
        }
        
        System.out.println("\n🚫 IP1 ahora está BLOQUEADA (0/5 fichas restantes)");

        // Intento desde otra IP debe funcionar (no estar bloqueado)
        System.out.println("\n🔄 Intentando login desde IP2 (192.168.1.201)...");
        
        AuthRequestDTO request = AuthRequestDTO.builder()
                .email(validEmail)
                .password("wrong_password")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", "192.168.1.201") // IP diferente
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // 400, no 429
                .andExpect(jsonPath("$.message").value("Credenciales invalidas"));
        
        System.out.println("   ✅ Respuesta: 400 Bad Request (NO 429)");
        System.out.println("   📊 IP2 tiene sus propias fichas: 4/5 restantes");
        System.out.println("   💬 Mensaje: \"Credenciales invalidas\" (no bloqueado)");
        System.out.println("\n✅ TEST 2 PASÓ: Cada IP tiene su propio bucket de rate limiting\n");
    }
}
