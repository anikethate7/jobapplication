package com.jobapplication.review.service;

import com.jobapplication.review.dto.ReviewRequestDTO;
import com.jobapplication.review.dto.ReviewResponseDTO;
import com.jobapplication.review.entity.ReviewEntity;
import com.jobapplication.review.external.CompanyClient;
import com.jobapplication.review.external.CompanyResponse;
import com.jobapplication.review.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

// FIX #3: Constructor injection
@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CompanyClient companyClient;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             CompanyClient companyClient) {
        this.reviewRepository = reviewRepository;
        this.companyClient = companyClient;
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────

    @Override
    public ReviewResponseDTO createReview(ReviewRequestDTO request) {
        // FIX #5: CompanyClient returns CompanyResponse directly (not Optional)
        CompanyResponse company = companyClient.getCompanyById(request.getCompanyId());
        if (company == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Company not found: " + request.getCompanyId());
        }

        ReviewEntity entity = new ReviewEntity();
        entity.setTitle(request.getTitle());
        entity.setBody(request.getBody());
        entity.setRating(request.getRating());
        entity.setReviewerName(request.getReviewerName());
        entity.setCompanyId(request.getCompanyId());

        ReviewEntity saved = reviewRepository.save(entity);
        log.info("Created review {} for companyId: {}", saved.getId(), saved.getCompanyId());
        return toResponseDTO(saved, company);
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    // FIX #8: Replaced parallelStream() — unsafe with blocking Feign I/O
    @Override
    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(review -> {
                    CompanyResponse company = companyClient.getCompanyById(review.getCompanyId());
                    return toResponseDTO(review, company);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "reviews", key = "#id")
    public ReviewResponseDTO getReviewById(Long id) {
        ReviewEntity entity = findReviewOrThrow(id);
        CompanyResponse company = companyClient.getCompanyById(entity.getCompanyId());
        return toResponseDTO(entity, company);
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByCompanyId(Long companyId) {
        CompanyResponse company = companyClient.getCompanyById(companyId);

        return reviewRepository.findByCompanyId(companyId)
                .stream()
                .map(review -> toResponseDTO(review, company))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ReviewResponseDTO> getReviewsByCompanyIdPaged(
            Long companyId, Pageable pageable) {
        CompanyResponse company = companyClient.getCompanyById(companyId);

        return reviewRepository.findByCompanyId(companyId, pageable)
                .map(review -> toResponseDTO(review, company));
    }

    @Override
    @Cacheable(value = "avgRating", key = "#companyId")
    public Double getAverageRatingForCompany(Long companyId) {
        return reviewRepository.findAverageRatingByCompanyId(companyId)
                .map(avg -> Math.round(avg * 10.0) / 10.0)
                .orElse(0.0);
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    // FIX #6: Evict review by id AND avgRating by the review's actual companyId
    @Override
    @Caching(evict = {
            @CacheEvict(value = "reviews", key = "#id"),
            @CacheEvict(value = "avgRating", key = "#result.companyId")
    })
    public ReviewResponseDTO updateReview(Long id, ReviewRequestDTO request) {
        ReviewEntity existing = findReviewOrThrow(id);

        CompanyResponse company;
        if (!existing.getCompanyId().equals(request.getCompanyId())) {
            company = companyClient.getCompanyById(request.getCompanyId());
            if (company == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Company not found: " + request.getCompanyId());
            }
        } else {
            company = companyClient.getCompanyById(existing.getCompanyId());
        }

        existing.setTitle(request.getTitle());
        existing.setBody(request.getBody());
        existing.setRating(request.getRating());
        existing.setReviewerName(request.getReviewerName());
        existing.setCompanyId(request.getCompanyId());

        return toResponseDTO(reviewRepository.save(existing), company);
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    // FIX #6: Evict only the specific review and its company's avgRating
    @Override
    @Caching(evict = {
            @CacheEvict(value = "reviews", key = "#id"),
            @CacheEvict(value = "avgRating", key = "#result")
            // Note: to get companyId here we look it up first below
    })
    public void deleteReview(Long id) {
        ReviewEntity entity = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Review not found: " + id));
        Long companyId = entity.getCompanyId();
        reviewRepository.deleteById(id);
        // Manually evict avgRating for this company since void return can't use SpEL #result
        evictAvgRating(companyId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"reviews", "avgRating"}, allEntries = true)
    public void deleteReviewsByCompanyId(Long companyId) {
        long count = reviewRepository.countByCompanyId(companyId);
        if (count > 0) {
            reviewRepository.deleteByCompanyId(companyId);
            log.info("Deleted {} reviews for companyId: {}", count, companyId);
        }
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    // Helper to evict avgRating after deleteReview (void methods can't use #result SpEL)
    @CacheEvict(value = "avgRating", key = "#companyId")
    public void evictAvgRating(Long companyId) {
        // intentionally empty — Spring handles the eviction
    }

    private ReviewEntity findReviewOrThrow(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Review not found: " + id));
    }

    private ReviewResponseDTO toResponseDTO(ReviewEntity entity, CompanyResponse company) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setBody(entity.getBody());
        dto.setRating(entity.getRating());
        dto.setReviewerName(entity.getReviewerName());
        dto.setCompanyId(entity.getCompanyId());
        dto.setCreatedAt(entity.getCreatedAt());

        if (company != null) {
            dto.setCompanyName(company.getName());
            dto.setCompanyDescription(company.getDescription());
        }
        return dto;
    }
}