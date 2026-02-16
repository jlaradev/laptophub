package com.laptophub.backend.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de rate limiting para intentos de login.
 * Limita a 5 intentos cada 15 minutos por IP.
 */
@Service
public class LoginRateLimiterService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    /**
     * Verifica si una IP puede hacer un intento de login.
     * @param key Identificador único (normalmente la IP del cliente)
     * @return true si puede hacer el intento, false si excedió el límite
     */
    public boolean tryConsume(String key) {
        Bucket bucket = resolveBucket(key);
        return bucket.tryConsume(1);
    }

    /**
     * Obtiene o crea un bucket para una IP específica.
     * Configuración: 5 intentos cada 15 minutos
     */
    private Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket());
    }

    private Bucket createNewBucket() {
        // Límite: 5 tokens, se recarga 1 token cada 3 minutos (5 tokens en 15 minutos)
        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillIntervally(1, Duration.ofMinutes(3))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Limpia buckets viejos del caché (opcional, para evitar memory leaks)
     * Puede ser llamado por un scheduled job
     */
    public void cleanupCache() {
        // En producción, podrías usar una cache con TTL como Caffeine o Redis
        if (cache.size() > 10000) {
            cache.clear();
        }
    }
}
