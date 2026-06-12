package com.jobapplication.review.service;

import com.jobapplication.review.dto.ReviewRequestDTO;
import com.jobapplication.review.dto.ReviewResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    ReviewResponseDTO createReview(ReviewRequestDTO request);

    List<ReviewResponseDTO> getAllReviews();

    ReviewResponseDTO getReviewById(Long id);

    List<ReviewResponseDTO> getReviewsByCompanyId(Long companyId);

    Page<ReviewResponseDTO> getReviewsByCompanyIdPaged(Long companyId, Pageable pageable);

    Double getAverageRatingForCompany(Long companyId);

    ReviewResponseDTO updateReview(Long id, ReviewRequestDTO request);

    void deleteReview(Long id);

    void deleteReviewsByCompanyId(Long companyId);
}