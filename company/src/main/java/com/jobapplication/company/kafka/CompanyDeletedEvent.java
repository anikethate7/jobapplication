package com.jobapplication.company.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// FIX #2: Proper event POJO — replaces manual JSON string concatenation
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDeletedEvent {
    private Long companyId;
}