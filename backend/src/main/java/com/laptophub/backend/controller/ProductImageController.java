package com.laptophub.backend.controller;

import com.laptophub.backend.model.Product;
import com.laptophub.backend.model.ProductImage;
import com.laptophub.backend.repository.ProductImageRepository;
import com.laptophub.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductImageController {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Agregar imagen a un producto
     * POST /api/products/{productId}/images
     */
    @PostMapping("/{productId}/images")
    @SuppressWarnings("null")
    public ResponseEntity<ProductImage> addImage(
            @PathVariable Long productId,
            @RequestParam String url,
            @RequestParam Integer orden,
            @RequestParam(required = false) String descripcion) {
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        ProductImage image = ProductImage.builder()
                .url(url)
                .orden(orden)
                .descripcion(descripcion)
                .product(product)
                .build();
        
        return ResponseEntity.ok(productImageRepository.save(image));
    }

    /**
     * Obtener todas las imágenes de un producto
     * GET /api/products/{productId}/images
     */
    @GetMapping("/{productId}/images")
    public ResponseEntity<List<ProductImage>> getImagesByProduct(@PathVariable Long productId) {
        List<ProductImage> images = productImageRepository.findByProductIdOrderByOrdenAsc(productId);
        return ResponseEntity.ok(images);
    }

    /**
     * Obtener imagen específica por ID
     * GET /api/products/images/{imageId}
     */
    @GetMapping("/images/{imageId}")
    @SuppressWarnings("null")
    public ResponseEntity<ProductImage> getImageById(@PathVariable Long imageId) {
        return productImageRepository.findById(imageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualizar imagen (orden, url, descripción)
     * PUT /api/products/images/{imageId}
     */
    @PutMapping("/images/{imageId}")
    @SuppressWarnings("null")
    public ResponseEntity<ProductImage> updateImage(
            @PathVariable Long imageId,
            @RequestParam(required = false) String url,
            @RequestParam(required = false) Integer orden,
            @RequestParam(required = false) String descripcion) {
        
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        if (url != null) image.setUrl(url);
        if (orden != null) image.setOrden(orden);
        if (descripcion != null) image.setDescripcion(descripcion);
        
        return ResponseEntity.ok(productImageRepository.save(image));
    }

    /**
     * Eliminar imagen
     * DELETE /api/products/images/{imageId}
     */
    @DeleteMapping("/images/{imageId}")
    @SuppressWarnings("null")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        if (!productImageRepository.existsById(imageId)) {
            return ResponseEntity.notFound().build();
        }
        productImageRepository.deleteById(imageId);
        return ResponseEntity.ok().build();
    }

    /**
     * Eliminar todas las imágenes de un producto
     * DELETE /api/products/{productId}/images
     */
    @DeleteMapping("/{productId}/images")
    public ResponseEntity<Void> deleteAllImagesByProduct(@PathVariable Long productId) {
        productImageRepository.deleteByProductId(productId);
        return ResponseEntity.ok().build();
    }
}
