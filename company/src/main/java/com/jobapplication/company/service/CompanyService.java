package com.jobapplication.company.service;

import com.jobapplication.company.dto.CompanyListDTO;
import com.jobapplication.company.dto.CompanyRequestDTO;
import com.jobapplication.company.dto.CompanyResponseDTO;

import java.util.List;

public interface CompanyService {

    CompanyResponseDTO createCompany(CompanyRequestDTO request);

    List<CompanyListDTO> getAllCompanies();

    CompanyResponseDTO getCompanyById(long id);

    CompanyResponseDTO updateCompany(long id, CompanyRequestDTO request);

    void deleteCompany(long id);
}