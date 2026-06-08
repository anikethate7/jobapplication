package com.jobapplication.reviews.review.service;

import com.jobapplication.reviews.review.dto.ReviewDTO;
import com.jobapplication.reviews.review.dto.ReviewRequestDTO;
import com.jobapplication.reviews.review.entity.ReviewEntity;
import com.jobapplication.reviews.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;


    @Override
    public List<ReviewDTO> getALlReviews(Long companyId) {
        return reviewRepository.findByCompanyId(companyId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO createReview(Long companyId, ReviewRequestDTO dto) {
        ReviewEntity entity = new ReviewEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setRating(dto.getRating());
        entity.setCompanyId(companyId);
        return toDTO(reviewRepository.save(entity));
    }

    @Override
    public ReviewDTO getReviewById(Long companyId, Long reviewId) {
        return reviewRepository.findById(reviewId)
                .filter(r -> companyId.equals(r.getCompanyId()))
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Review " + reviewId + " not found for company " + companyId));
    }

    @Override
    public ReviewDTO updateReview(Long companyId, Long reviewId, ReviewRequestDTO dto) {
        ReviewEntity existing = reviewRepository.findById(reviewId)
                .filter(r -> companyId.equals(r.getCompanyId()))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Review " + reviewId + " not found for company " + companyId));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setRating(dto.getRating());
        return toDTO(reviewRepository.save(existing));
    }

    @Override
    public boolean deleteReview(Long companyId, Long reviewId) {
        ReviewEntity entity = reviewRepository.findById(reviewId)
                .filter(r -> companyId.equals(r.getCompanyId()))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Review " + reviewId + " not found for company " + companyId));

        reviewRepository.delete(entity);
        return true;
    }

    private ReviewDTO toDTO(ReviewEntity entity) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setRating(entity.getRating());
        dto.setCompanyId(entity.getCompanyId());
        return dto;
    }
}