package com.laptophub.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Prueba completa del endpoint /api/status
     * Verifica: status 200 OK, mensaje correcto y content-type
     */
    @Test
    public void testCheckStatusEndpoint() throws Exception {
        System.out.println("\n=== Probando endpoint GET /api/status ===");
        mockMvc.perform(get("/api/status"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("Servidor de LaptopHub funcionando correctamente y conectado a Neon!"));
        System.out.println("✅ TEST PASÓ: Status 200, content-type correcto, mensaje correcto\n");
    }
}
