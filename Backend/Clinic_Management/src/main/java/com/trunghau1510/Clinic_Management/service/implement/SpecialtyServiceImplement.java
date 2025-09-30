package com.trunghau1510.Clinic_Management.service.implement;

import com.trunghau1510.Clinic_Management.dto.response.SpecialtyResponse;
import com.trunghau1510.Clinic_Management.entity.Specialty;
import com.trunghau1510.Clinic_Management.mapper.SpecialtyMapper;
import com.trunghau1510.Clinic_Management.repository.SpecialtyRepository;
import com.trunghau1510.Clinic_Management.service.SpecialtyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpecialtyServiceImplement implements SpecialtyService {

    SpecialtyRepository specialtyRepository;
    SpecialtyMapper specialtyMapper;

    @Override
    public Specialty getSpecialtyById(String id) {
        return specialtyRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Specialty> getAllSpecialty(Pageable pageable) {
        return specialtyRepository.findAll(pageable);
    }

    @Override
    public List<Specialty> getAllSpecialty() {
        return specialtyRepository.findAll();
    }

    @Override
    public Specialty addOrUpdate(Specialty specialty) {
        if (specialty.getId() != null && !specialty.getId().isEmpty()) {
            Specialty existingSpecialty = specialtyRepository.findById(specialty.getId()).orElse(null);
            if (existingSpecialty != null) {
                existingSpecialty.setName(specialty.getName());
                existingSpecialty.setDescription(specialty.getDescription());
                return specialtyRepository.save(existingSpecialty);
            }
        }
        // Tạo mới - clear id để đảm bảo tạo mới
        specialty.setId(null);
        return specialtyRepository.save(specialty);
    }

    @Override
    public boolean deleteSpecialty(String id) {
        try {
            if (specialtyRepository.existsById(id)) {
                specialtyRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<SpecialtyResponse> getAllSpecialtyResponses() {
        return specialtyRepository.findAll().stream()
            .map(specialtyMapper::toSpecialtyResponse)
            .collect(Collectors.toList());
    }
}
