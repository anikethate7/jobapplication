package com.jobapplication.company.company.controller;

import com.jobapplication.company.company.dto.CompanyRequestDTO;
import com.jobapplication.company.company.dto.CompanyResponseDTO;
import com.jobapplication.company.company.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompany() {
        return ResponseEntity.ok(companyService.getAllCompany());
    }

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> addCompany(@RequestBody CompanyRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.createCompany(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(
            @PathVariable Long id,
            @RequestBody CompanyRequestDTO request) {
        CompanyResponseDTO updated = companyService.updateCompany(id, request);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(@PathVariable long id) {
        CompanyResponseDTO company = companyService.getCompanyById(id);
        if (company == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable long id) {
        boolean isDeleted = companyService.deleteCompany(id);
        if (isDeleted) return ResponseEntity.ok("Company with id " + id + " deleted successfully");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Company with id " + id + " not found");
    }
}