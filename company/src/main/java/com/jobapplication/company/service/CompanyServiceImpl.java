package com.jobapplication.company.service;

import com.jobapplication.company.dto.CompanyListDTO;
import com.jobapplication.company.dto.CompanyRequestDTO;
import com.jobapplication.company.dto.CompanyResponseDTO;
import com.jobapplication.company.entity.Company;
import com.jobapplication.company.external.JobClient;
import com.jobapplication.company.external.ReviewClient;
import com.jobapplication.company.kafka.CompanyEventProducer;
import com.jobapplication.company.repository.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

// FIX #3: Constructor injection throughout
@Service
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final JobClient jobClient;
    private final ReviewClient reviewClient;
    private final CompanyEventProducer companyEventProducer;

    public CompanyServiceImpl(CompanyRepository companyRepository,
                              JobClient jobClient,
                              ReviewClient reviewClient,
                              CompanyEventProducer companyEventProducer) {
        this.companyRepository = companyRepository;
        this.jobClient = jobClient;
        this.reviewClient = reviewClient;
        this.companyEventProducer = companyEventProducer;
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────

    @Override
    public CompanyResponseDTO createCompany(CompanyRequestDTO request) {
        Company company = new Company();
        company.setName(request.getName());
        company.setDescription(request.getDescription());
        return toResponseDTO(companyRepository.save(company));
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    // FIX #4: Returns lightweight CompanyListDTO — no Feign calls for list
    @Override
    public List<CompanyListDTO> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "companies", key = "#id")
    public CompanyResponseDTO getCompanyById(long id) {
        Company company = findCompanyOrThrow(id);
        return toResponseDTO(company);
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    @Override
    @CacheEvict(value = "companies", key = "#id")
    public CompanyResponseDTO updateCompany(long id, CompanyRequestDTO request) {
        Company existing = findCompanyOrThrow(id);
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        return toResponseDTO(companyRepository.save(existing));
    }

    // ─── DELETE (cascade via Kafka) ───────────────────────────────────────────

    // FIX #1: Event is published AFTER successful DB delete, not before.
    // If deleteById throws, the event is never published — no dangling cascade.
    // Removed @Transactional because we must not roll back after the event is sent.
    @Override
    @CacheEvict(value = "companies", key = "#id")
    public void deleteCompany(long id) {
        findCompanyOrThrow(id);
        companyRepository.deleteById(id);
        // Publish only after successful persistence operation
        companyEventProducer.publishCompanyDeleted(id);
        log.info("Deleted company: {} and published cascade event", id);
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    private Company findCompanyOrThrow(long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Company not found: " + id));
    }

    // FIX #4: Lightweight mapper for list — no external calls
    private CompanyListDTO toListDTO(Company company) {
        CompanyListDTO dto = new CompanyListDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setCreatedAt(company.getCreatedAt());
        return dto;
    }

    private CompanyResponseDTO toResponseDTO(Company company) {
        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setCreatedAt(company.getCreatedAt());

        // Enriched from other services (graceful fallback on failure)
        dto.setJobs(jobClient.getJobsByCompany(company.getId()));
        dto.setReviews(reviewClient.getReviewsByCompany(company.getId()));
        dto.setAverageRating(reviewClient.getAverageRating(company.getId()));

        return dto;
    }
}