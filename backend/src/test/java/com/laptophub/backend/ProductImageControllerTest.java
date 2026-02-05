package com.laptophub.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laptophub.backend.model.Product;
import com.laptophub.backend.model.ProductImage;
import com.laptophub.backend.repository.CartItemRepository;
import com.laptophub.backend.repository.OrderItemRepository;
import com.laptophub.backend.repository.ProductImageRepository;
import com.laptophub.backend.repository.ProductRepository;
import com.laptophub.backend.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Tests de endpoints CRUD para ProductImage
 * Ejecuta las pruebas en orden específico para mantener consistencia
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private static String productId;
    private static String imageId1;
    private static String imageId2;

    /**
     * Limpia la base de datos una sola vez antes de todos los tests
     */
    @BeforeAll
    public static void setUpDatabase() {
        // La limpieza ocurre una sola vez al inicio
    }

    /**
     * TEST 1: Configuración - Crear producto de prueba
     */
    @Test
    @Order(1)
    @SuppressWarnings("null")
    public void test1_SetupProduct() throws Exception {
        // Limpiar BD solo antes del primer test (respetar orden de foreign keys)
        cartItemRepository.deleteAll();
        reviewRepository.deleteAll();
        orderItemRepository.deleteAll();
        productImageRepository.deleteAll();
        productRepository.deleteAll();
        
        System.out.println("\n=== TEST 1: Configuración - Crear producto ===");
        
        Product testProduct = Product.builder()
                .nombre("Laptop ASUS ROG Strix")
                .descripcion("Laptop gaming de alto rendimiento")
                .precio(new BigDecimal("1799.99"))
                .stock(15)
                .marca("ASUS")
                .procesador("AMD Ryzen 9 5900HX")
                .ram(32)
                .almacenamiento(1024)
                .pantalla("17.3 pulgadas QHD 165Hz")
                .gpu("NVIDIA RTX 3070")
                .peso(new BigDecimal("2.7"))
                .build();

        MvcResult result = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Product createdProduct = objectMapper.readValue(response, Product.class);
        productId = createdProduct.getId().toString();
        
        System.out.println("✅ TEST 1 PASÓ: Producto creado con ID: " + productId + "\n");
    }

    /**
     * TEST 2: Agregar primera imagen al producto (POST /api/products/{productId}/images)
     */
    @Test
    @Order(2)
    public void test2_AddFirstImage() throws Exception {
        System.out.println("\n=== TEST 2: Agregar primera imagen al producto ===");
        
        MvcResult result = mockMvc.perform(post("/api/products/" + productId + "/images")
                        .param("url", "https://example.com/asus-rog-front.jpg")
                        .param("orden", "1")
                        .param("descripcion", "Vista frontal"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.url").value("https://example.com/asus-rog-front.jpg"))
                .andExpect(jsonPath("$.orden").value(1))
                .andExpect(jsonPath("$.descripcion").value("Vista frontal"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        ProductImage createdImage = objectMapper.readValue(response, ProductImage.class);
        imageId1 = createdImage.getId().toString();
        
        System.out.println("✅ TEST 2 PASÓ: Primera imagen creada con ID: " + imageId1 + "\n");
    }

    /**
     * TEST 3: Agregar segunda imagen al producto
     */
    @Test
    @Order(3)
    public void test3_AddSecondImage() throws Exception {
        System.out.println("\n=== TEST 3: Agregar segunda imagen al producto ===");
        
        MvcResult result = mockMvc.perform(post("/api/products/" + productId + "/images")
                        .param("url", "https://example.com/asus-rog-side.jpg")
                        .param("orden", "2")
                        .param("descripcion", "Vista lateral"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.orden").value(2))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        ProductImage createdImage = objectMapper.readValue(response, ProductImage.class);
        imageId2 = createdImage.getId().toString();
        
        System.out.println("✅ TEST 3 PASÓ: Segunda imagen creada con ID: " + imageId2 + "\n");
    }

    /**
     * TEST 4: Obtener todas las imágenes del producto (GET /api/products/{productId}/images)
     */
    @Test
    @Order(4)
    public void test4_GetImagesByProduct() throws Exception {
        System.out.println("\n=== TEST 4: Obtener todas las imágenes del producto ===");
        
        mockMvc.perform(get("/api/products/" + productId + "/images"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orden").value(1))
                .andExpect(jsonPath("$[1].orden").value(2));
        
        System.out.println("✅ TEST 4 PASÓ: Imágenes obtenidas correctamente\n");
    }

    /**
     * TEST 5: Obtener imagen específica por ID (GET /api/products/images/{imageId})
     */
    @Test
    @Order(5)
    public void test5_GetImageById() throws Exception {
        System.out.println("\n=== TEST 5: Obtener imagen por ID ===");
        
        mockMvc.perform(get("/api/products/images/" + imageId1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(imageId1))
                .andExpect(jsonPath("$.url").value("https://example.com/asus-rog-front.jpg"))
                .andExpect(jsonPath("$.descripcion").value("Vista frontal"));
        
        System.out.println("✅ TEST 5 PASÓ: Imagen encontrada por ID\n");
    }

    /**
     * TEST 6: Actualizar imagen (PUT /api/products/images/{imageId})
     */
    @Test
    @Order(6)
    public void test6_UpdateImage() throws Exception {
        System.out.println("\n=== TEST 6: Actualizar imagen ===");
        
        mockMvc.perform(put("/api/products/images/" + imageId1)
                        .param("url", "https://example.com/asus-rog-front-updated.jpg")
                        .param("descripcion", "Vista frontal actualizada"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(imageId1))
                .andExpect(jsonPath("$.url").value("https://example.com/asus-rog-front-updated.jpg"))
                .andExpect(jsonPath("$.descripcion").value("Vista frontal actualizada"));
        
        System.out.println("✅ TEST 6 PASÓ: Imagen actualizada correctamente\n");
    }

    /**
     * TEST 7: Eliminar una imagen (DELETE /api/products/images/{imageId})
     */
    @Test
    @Order(7)
    public void test7_DeleteImage() throws Exception {
        System.out.println("\n=== TEST 7: Eliminar imagen ===");
        
        mockMvc.perform(delete("/api/products/images/" + imageId2))
                .andDo(print())
                .andExpect(status().isOk());
        
        // Verificar que solo queda 1 imagen
        mockMvc.perform(get("/api/products/" + productId + "/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        
        System.out.println("✅ TEST 7 PASÓ: Imagen eliminada correctamente\n");
    }

    /**
     * TEST 8: Crear imágenes finales para verificación manual
     */
    @Test
    @Order(8)
    public void test8_CreateFinalImagesForVerification() throws Exception {
        System.out.println("\n=== TEST 8: Crear imágenes finales para verificación manual ===");
        
        // Agregar tercera imagen
        mockMvc.perform(post("/api/products/" + productId + "/images")
                        .param("url", "https://example.com/asus-rog-keyboard.jpg")
                        .param("orden", "3")
                        .param("descripcion", "Vista del teclado RGB"))
                .andExpect(status().isOk());
        
        // Agregar cuarta imagen
        mockMvc.perform(post("/api/products/" + productId + "/images")
                        .param("url", "https://example.com/asus-rog-ports.jpg")
                        .param("orden", "4")
                        .param("descripcion", "Puertos laterales"))
                .andExpect(status().isOk());
        
        System.out.println("✅ TEST 8 PASÓ: Imágenes finales creadas");
        System.out.println("📋 Verifica en tu gestor de BD las imágenes del producto ID: " + productId + "\n");
    }
}
