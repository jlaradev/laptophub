package com.laptophub.backend.controller;

import com.laptophub.backend.model.Product;
import com.laptophub.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Page<Product> findAll(Pageable pageable) {
        return productService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Product findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping("/search")
    public Page<Product> searchByName(@RequestParam String nombre, Pageable pageable) {
        return productService.searchByName(nombre, pageable);
    }

    @GetMapping("/brand")
    public Page<Product> findByBrand(@RequestParam String marca, Pageable pageable) {
        return productService.findByBrand(marca, pageable);
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}