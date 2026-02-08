package com.laptophub.backend.controller;

import com.laptophub.backend.model.Review;
import com.laptophub.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review create(
            @RequestParam Long productId,
            @RequestParam UUID userId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comentario
    ) {
        return reviewService.createReview(productId, userId, rating, comentario);
    }

    @GetMapping("/product/{productId}")
    public Page<Review> getByProduct(@PathVariable Long productId, Pageable pageable) {
        return reviewService.getReviewsByProduct(productId, pageable);
    }

    @GetMapping("/product/{productId}/user/{userId}")
    public Review getUserReview(
            @PathVariable Long productId,
            @PathVariable UUID userId
    ) {
        return reviewService.getUserReviewForProduct(productId, userId);
    }

    @PutMapping("/{reviewId}")
    public Review update(
            @PathVariable Long reviewId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comentario
    ) {
        return reviewService.updateReview(reviewId, rating, comentario);
    }

    @DeleteMapping("/{reviewId}")
    public void delete(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
    }

    @GetMapping("/product/{productId}/average")
    public Double average(@PathVariable Long productId) {
        return reviewService.getAverageRating(productId);
    }
}