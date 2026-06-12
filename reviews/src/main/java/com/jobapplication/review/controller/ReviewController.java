package com.jobapplication.review.controller;

import com.jobapplication.review.dto.ReviewRequestDTO;
import com.jobapplication.review.dto.ReviewResponseDTO;
import com.jobapplication.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// FIX #3: Constructor injection
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(
            @Valid @RequestBody ReviewRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(request));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByCompany(
            @PathVariable Long companyId) {
        return ResponseEntity.ok(reviewService.getReviewsByCompanyId(companyId));
    }

    // GET /reviews/company/1/paged?page=0&size=10&sortBy=createdAt&direction=desc
    @GetMapping("/company/{companyId}/paged")
    public ResponseEntity<Page<ReviewResponseDTO>> getReviewsByCompanyPaged(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(
                reviewService.getReviewsByCompanyIdPaged(companyId, pageable));
    }

    @GetMapping("/company/{companyId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long companyId) {
        return ResponseEntity.ok(reviewService.getAverageRatingForCompany(companyId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDTO request) {
        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}