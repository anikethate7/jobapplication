package com.jobapplication.company.company.repository;

import com.jobapplication.company.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
