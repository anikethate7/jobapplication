package com.jobapplication.reviews.review.controller;

import com.jobapplication.reviews.review.dto.ReviewDTO;
import com.jobapplication.reviews.review.dto.ReviewRequestDTO;
import com.jobapplication.reviews.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/companies/{companyId}")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDTO>> getAll(@PathVariable Long companyId) {
        return ResponseEntity.ok(reviewService.getALlReviews(companyId));
    }
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewDTO> getById(@PathVariable Long companyId,
                                             @PathVariable Long reviewId) {
        ReviewDTO dto = reviewService.getReviewById(companyId, reviewId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewDTO> create(@PathVariable Long companyId,
                                            @RequestBody ReviewRequestDTO dto) {
        ReviewDTO created = reviewService.createReview(companyId, dto);
        if (created == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("reviews/{reviewId}")
    public ResponseEntity<ReviewDTO> update(@PathVariable Long companyId,
                                            @PathVariable Long reviewId,
                                            @RequestBody ReviewRequestDTO dto) {
        ReviewDTO updated = reviewService.updateReview(companyId, reviewId, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("reviews/{reviewId}")
    public ResponseEntity<String> delete(@PathVariable Long companyId,
                                         @PathVariable Long reviewId) {
        return reviewService.deleteReview(companyId, reviewId)
                ? ResponseEntity.ok("Review " + reviewId + " deleted successfully")
                : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Review " + reviewId + " not found for company " + companyId);
    }
}
