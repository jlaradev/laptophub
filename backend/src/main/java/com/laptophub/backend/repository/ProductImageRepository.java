package com.laptophub.backend.repository;

import com.laptophub.backend.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    /**
     * Encuentra todas las imágenes de un producto ordenadas por su orden
     */
    List<ProductImage> findByProductIdOrderByOrdenAsc(Long productId);
    
    /**
     * Elimina todas las imágenes de un producto
     */
    void deleteByProductId(Long productId);
}
