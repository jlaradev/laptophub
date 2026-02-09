package com.laptophub.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para verificar conectividad a las 3 bases de datos
 */
@SpringBootTest
public class DatabaseConnectionTest {

    @Value("${DB_URL_DEV}")
    private String devUrl;
    
    @Value("${DB_USERNAME_DEV}")
    private String devUsername;
    
    @Value("${DB_PASSWORD_DEV}")
    private String devPassword;

    @Value("${DB_URL_PROD}")
    private String prodUrl;
    
    @Value("${DB_USERNAME_PROD}")
    private String prodUsername;
    
    @Value("${DB_PASSWORD_PROD}")
    private String prodPassword;

    @Value("${DB_URL_TEST}")
    private String testUrl;
    
    @Value("${DB_USERNAME_TEST}")
    private String testUsername;
    
    @Value("${DB_PASSWORD_TEST}")
    private String testPassword;

    @Test
    public void testDevDatabaseConnection() {
        System.out.println("\n🔵 Probando conexión a BD de DESARROLLO...");
        testConnection(devUrl, devUsername, devPassword, "DEV");
    }

    @Test
    public void testProdDatabaseConnection() {
        System.out.println("\n🟢 Probando conexión a BD de PRODUCCIÓN...");
        testConnection(prodUrl, prodUsername, prodPassword, "PROD");
    }

    @Test
    public void testTestDatabaseConnection() {
        System.out.println("\n🟡 Probando conexión a BD de TEST...");
        testConnection(testUrl, testUsername, testPassword, "TEST");
    }

    private void testConnection(String url, String username, String password, String env) {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            assertNotNull(conn, "Conexión no debe ser null");
            assertFalse(conn.isClosed(), "Conexión debe estar abierta");
            
            // Ejecutar query simple para verificar
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT version(), current_database(), current_user")) {
                
                if (rs.next()) {
                    String version = rs.getString(1);
                    String database = rs.getString(2);
                    String user = rs.getString(3);
                    
                    System.out.println("✅ Conexión exitosa a " + env);
                    System.out.println("   📊 Base de datos: " + database);
                    System.out.println("   👤 Usuario: " + user);
                    System.out.println("   🗄️  PostgreSQL: " + version.substring(0, Math.min(50, version.length())));
                }
            }
            
        } catch (Exception e) {
            fail("❌ Error conectando a " + env + ": " + e.getMessage());
        }
    }
}
