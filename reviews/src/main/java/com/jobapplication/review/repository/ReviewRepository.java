package com.jobapplication.review.repository;

import com.jobapplication.review.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findByCompanyId(Long companyId);

    void deleteByCompanyId(Long companyId);

    Page<ReviewEntity> findByCompanyId(Long companyId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.companyId = :companyId")
    Optional<Double> findAverageRatingByCompanyId(@Param("companyId") Long companyId);

    long countByCompanyId(Long companyId);
}