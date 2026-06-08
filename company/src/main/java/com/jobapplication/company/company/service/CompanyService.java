package com.jobapplication.company.company.service;

import com.jobapplication.company.company.dto.CompanyRequestDTO;
import com.jobapplication.company.company.dto.CompanyResponseDTO;
import java.util.List;

public interface CompanyService {
    List<CompanyResponseDTO> getAllCompany();
    CompanyResponseDTO getCompanyById(long id);
    CompanyResponseDTO createCompany(CompanyRequestDTO request);
    CompanyResponseDTO updateCompany(long id, CompanyRequestDTO request);
    boolean deleteCompany(long id);
}