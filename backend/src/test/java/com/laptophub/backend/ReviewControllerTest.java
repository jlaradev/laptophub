package com.laptophub.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptophub.backend.model.Product;
import com.laptophub.backend.model.Review;
import com.laptophub.backend.model.User;
import com.laptophub.backend.repository.ProductRepository;
import com.laptophub.backend.repository.ReviewRepository;
import com.laptophub.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Tests de endpoints CRUD para Review
 * Ejecuta las pruebas en orden específico para mantener consistencia
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private static String userId;
    private static String productId;
    private static String reviewId;

    /**
     * Limpia la base de datos una sola vez antes de todos los tests
     */
    @BeforeAll
    public static void setUpDatabase() {
        // La limpieza ocurre una sola vez al inicio
    }

    /**
     * TEST 1: Configuración - Crear usuario y producto de prueba
     */
    @Test
    @Order(1)
    @SuppressWarnings("null")
    public void test1_SetupUserAndProduct() throws Exception {
        // Limpiar BD solo antes del primer test
        reviewRepository.deleteAll();
        
        System.out.println("\n=== TEST 1: Configuración - Crear usuario y producto ===");
        
        // Crear usuario de prueba
        User testUser = User.builder()
                .email("review.test@laptophub.com")
                .password("password123")
                .nombre("Reviewer")
                .apellido("Test")
                .telefono("555-0002")
                .direccion("Review Test Address")
                .build();
        
        User savedUser = userRepository.save(testUser);
        userId = savedUser.getId().toString();
        
        // Crear producto de prueba
        Product testProduct = Product.builder()
                .nombre("Laptop Asus ROG Strix")
                .descripcion("Laptop gaming de alto rendimiento")
                .precio(new BigDecimal("1499.99"))
                .stock(30)
                .marca("ASUS")
                .procesador("AMD Ryzen 9 5900HX")
                .ram(32)
                .almacenamiento(1024)
                .pantalla("17.3 pulgadas QHD 165Hz")
                .gpu("NVIDIA RTX 3070")
                .peso(new BigDecimal("2.9"))
                .imagenUrl("https://example.com/asus-rog-strix.jpg")
                .build();
        
        Product savedProduct = productRepository.save(testProduct);
        productId = savedProduct.getId().toString();
        
        System.out.println("✅ TEST 1 PASÓ: Usuario creado con ID: " + userId);
        System.out.println("✅ Producto creado con ID: " + productId + "\n");
    }

    /**
     * TEST 2: Crear review (POST /api/reviews)
     */
    @Test
    @Order(2)
    public void test2_CreateReview() throws Exception {
        System.out.println("\n=== TEST 2: Crear nueva review (POST /api/reviews) ===");
        
        MvcResult result = mockMvc.perform(post("/api/reviews")
                        .param("productId", productId)
                        .param("userId", userId)
                        .param("rating", "5")
                        .param("comentario", "Excelente laptop, superó mis expectativas"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comentario").value("Excelente laptop, superó mis expectativas"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Review createdReview = objectMapper.readValue(response, Review.class);
        reviewId = createdReview.getId().toString();
        
        System.out.println("✅ TEST 2 PASÓ: Review creada con ID: " + reviewId + "\n");
    }

    /**
     * TEST 3: Obtener reviews por producto (GET /api/reviews/product/{productId})
     */
    @Test
    @Order(3)
    public void test3_GetReviewsByProduct() throws Exception {
        System.out.println("\n=== TEST 3: Obtener reviews por producto (GET /api/reviews/product/{productId}) ===");
        
        mockMvc.perform(get("/api/reviews/product/" + productId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].rating").value(5));
        
        System.out.println("✅ TEST 3 PASÓ: Reviews obtenidas por producto\n");
    }

    /**
     * TEST 4: Obtener review específica de usuario para producto (GET /api/reviews/product/{productId}/user/{userId})
     */
    @Test
    @Order(4)
    public void test4_GetUserReviewForProduct() throws Exception {
        System.out.println("\n=== TEST 4: Obtener review de usuario específico (GET /api/reviews/product/{productId}/user/{userId}) ===");
        
        mockMvc.perform(get("/api/reviews/product/" + productId + "/user/" + userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comentario").value("Excelente laptop, superó mis expectativas"));
        
        System.out.println("✅ TEST 4 PASÓ: Review específica del usuario obtenida\n");
    }

    /**
     * TEST 5: Actualizar review (PUT /api/reviews/{reviewId})
     */
    @Test
    @Order(5)
    public void test5_UpdateReview() throws Exception {
        System.out.println("\n=== TEST 5: Actualizar review (PUT /api/reviews/{reviewId}) ===");
        
        mockMvc.perform(put("/api/reviews/" + reviewId)
                        .param("rating", "4")
                        .param("comentario", "Muy buena laptop, solo el ventilador es un poco ruidoso"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comentario").value("Muy buena laptop, solo el ventilador es un poco ruidoso"));
        
        System.out.println("✅ TEST 5 PASÓ: Review actualizada correctamente\n");
    }

    /**
     * TEST 6: Calcular promedio de ratings (GET /api/reviews/product/{productId}/average)
     */
    @Test
    @Order(6)
    public void test6_CalculateAverageRating() throws Exception {
        System.out.println("\n=== TEST 6: Calcular promedio de ratings (GET /api/reviews/product/{productId}/average) ===");
        
        mockMvc.perform(get("/api/reviews/product/" + productId + "/average"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
        
        System.out.println("✅ TEST 6 PASÓ: Promedio de ratings calculado\n");
    }

    /**
     * TEST 7: Eliminar review (DELETE /api/reviews/{reviewId})
     */
    @Test
    @Order(7)
    public void test7_DeleteReview() throws Exception {
        System.out.println("\n=== TEST 7: Eliminar review (DELETE /api/reviews/{reviewId}) ===");
        
        mockMvc.perform(delete("/api/reviews/" + reviewId))
                .andDo(print())
                .andExpect(status().isOk());
        
        System.out.println("✅ TEST 7 PASÓ: Review eliminada correctamente\n");
    }

    /**
     * TEST 8: Crear review final para verificación manual
     */
    @Test
    @Order(8)
    public void test8_CreateFinalReviewForVerification() throws Exception {
        System.out.println("\n=== TEST 8: Crear review final para verificación manual ===");
        
        MvcResult result = mockMvc.perform(post("/api/reviews")
                        .param("productId", productId)
                        .param("userId", userId)
                        .param("rating", "5")
                        .param("comentario", "Producto verificado - Review final de prueba"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.rating").value(5))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Review createdReview = objectMapper.readValue(response, Review.class);
        
        System.out.println("✅ TEST 8 PASÓ: Review final creada con ID: " + createdReview.getId());
        System.out.println("📋 Verifica en tu gestor de BD la review del usuario: review.test@laptophub.com\n");
    }
}
