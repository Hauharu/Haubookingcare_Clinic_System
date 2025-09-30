package com.trunghau1510.Clinic_Management.service;

import com.trunghau1510.Clinic_Management.dto.response.SpecialtyResponse;
import com.trunghau1510.Clinic_Management.entity.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SpecialtyService {

    Specialty getSpecialtyById(String id);
    Specialty addOrUpdate(Specialty specialty);
    boolean deleteSpecialty(String id);
    Page<Specialty> getAllSpecialty(Pageable pageable);
    List<Specialty> getAllSpecialty();
    List<SpecialtyResponse> getAllSpecialtyResponses();
}
