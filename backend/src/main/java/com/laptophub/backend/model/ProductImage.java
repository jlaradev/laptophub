package com.laptophub.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa una imagen de producto
 * Permite múltiples imágenes por producto con orden y descripción
 */
@Entity
@Table(name = "product_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private Integer orden; // 1 = principal, 2 = segunda, etc.

    @Column(length = 200)
    private String descripcion; // Opcional: "Vista frontal", "Puertos laterales", etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;
}
