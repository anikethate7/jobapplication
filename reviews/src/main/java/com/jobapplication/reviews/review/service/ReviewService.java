package com.jobapplication.reviews.review.service;


import com.jobapplication.reviews.review.dto.ReviewDTO;
import com.jobapplication.reviews.review.dto.ReviewRequestDTO;

import java.util.List;

public interface ReviewService {

    List<ReviewDTO> getALlReviews(Long companyId);
    ReviewDTO createReview(Long companyId, ReviewRequestDTO dto);
    ReviewDTO getReviewById(Long companyId, Long reviewId);
    ReviewDTO updateReview(Long companyId, Long reviewId, ReviewRequestDTO dto);
    boolean deleteReview(Long companyId, Long reviewId);
}
