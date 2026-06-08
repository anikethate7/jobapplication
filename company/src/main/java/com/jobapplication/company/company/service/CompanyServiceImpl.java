package com.jobapplication.company.company.service;

import com.jobapplication.company.company.dto.CompanyRequestDTO;
import com.jobapplication.company.company.dto.CompanyResponseDTO;
import com.jobapplication.company.company.entity.Company;
import com.jobapplication.company.company.external.JobResponse;
import com.jobapplication.company.company.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private static final String JOB_SERVICE_URL = "http://JOB-SERVICE/jobs/company/";

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<CompanyResponseDTO> getAllCompany() {
        return companyRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponseDTO getCompanyById(long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found: " + id));
        return toResponseDTO(company);
    }

    @Override
    public CompanyResponseDTO createCompany(CompanyRequestDTO request) {
        Company company = new Company();
        company.setName(request.getName());
        company.setDescription(request.getDescription());
        return toResponseDTO(companyRepository.save(company));
    }

    @Override
    public CompanyResponseDTO updateCompany(long id, CompanyRequestDTO request) {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found: " + id));
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        return toResponseDTO(companyRepository.save(existing));
    }

    @Override
    public boolean deleteCompany(long id) {
        if (!companyRepository.existsById(id)) {
            throw new EntityNotFoundException("Company not found: " + id);
        }
        companyRepository.deleteById(id);
        return true;
    }


    private CompanyResponseDTO toResponseDTO(Company company) {
        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setJobs(fetchJobsForCompany(company.getId()));
        return dto;
    }

    private List<JobResponse> fetchJobsForCompany(Long companyId) {
        try {
            ResponseEntity<List<JobResponse>> response = restTemplate.exchange(
                    JOB_SERVICE_URL + companyId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<JobResponse>>() {}
            );
            List<JobResponse> jobs = response.getBody();
            return jobs != null ? jobs : Collections.emptyList();
        } catch (RestClientException e) {
            // Job service unavailable — degrade gracefully, return empty list
            return Collections.emptyList();
        }
    }
}