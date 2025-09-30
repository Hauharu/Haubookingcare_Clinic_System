package com.trunghau1510.Clinic_Management.controller;

import lombok.AccessLevel;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.trunghau1510.Clinic_Management.entity.Specialty;
import com.trunghau1510.Clinic_Management.utils.PaginationUtils;
import com.trunghau1510.Clinic_Management.config.PaginationConfig;
import com.trunghau1510.Clinic_Management.service.SpecialtyService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.trunghau1510.Clinic_Management.dto.request.SpecialtyCreationRequest;
import com.trunghau1510.Clinic_Management.dto.request.SpecialtyUpdateRequest;
import com.trunghau1510.Clinic_Management.mapper.SpecialtyMapper;

@Controller
@RequiredArgsConstructor
@RequestMapping("/specialty")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpecialtyController {

    SpecialtyService specialtyService;
    PaginationConfig paginationConfig;
    SpecialtyMapper specialtyMapper;

    @GetMapping("/create-specialty")
    public String createSpecialtyForm(Model model) {
        model.addAttribute("specialty", new SpecialtyCreationRequest());
        return "specialty/create-specialty";
    }

    @PostMapping("/create-specialty")
    public String createSpecialty(@ModelAttribute SpecialtyCreationRequest specialtyRequest, RedirectAttributes redirectAttrs) {
        Specialty specialty = specialtyMapper.toSpecialty(specialtyRequest);
        boolean isUpdate = specialty.getId() != null && !specialty.getId().trim().isEmpty();
        Specialty result = specialtyService.addOrUpdate(specialty);

        if (result != null) {
            redirectAttrs.addFlashAttribute("successMessage",
                    isUpdate ? "Cập nhật chuyên khoa thành công!" : "Tạo chuyên khoa thành công!");
        } else {
            redirectAttrs.addFlashAttribute("errorMessage",
                    isUpdate ? "Cập nhật chuyên khoa thất bại!" : "Tạo mới chuyên khoa thất bại!");
        }
        return "redirect:/specialty";
    }

    @GetMapping("/update-specialty/{id}")
    public String updateSpecialtyForm(@PathVariable String id, Model model, RedirectAttributes redirectAttrs) {
        Specialty specialty = specialtyService.getSpecialtyById(id);
        if (specialty != null) {
            SpecialtyUpdateRequest request = new SpecialtyUpdateRequest();
            request.setId(specialty.getId());
            request.setName(specialty.getName());
            request.setDescription(specialty.getDescription());
            model.addAttribute("specialty", request);
            model.addAttribute("isUpdate", true);
            return "specialty/create-specialty";
        }
        redirectAttrs.addFlashAttribute("errorMessage", "Không tìm thấy chuyên khoa!");
        return "redirect:/specialty";
    }

    @PostMapping("/update-specialty")
    public String updateSpecialty(@ModelAttribute SpecialtyUpdateRequest specialtyRequest, RedirectAttributes redirectAttrs) {
        Specialty specialty = specialtyMapper.toSpecialty(specialtyRequest);
        Specialty result = specialtyService.addOrUpdate(specialty);
        if (result != null) {
            redirectAttrs.addFlashAttribute("successMessage", "Cập nhật chuyên khoa thành công!");
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Cập nhật chuyên khoa thất bại!");
        }
        return "redirect:/specialty";
    }

    @PostMapping("/delete-specialty/{id}")
    public String deleteSpecialty(@PathVariable String id, RedirectAttributes redirectAttrs) {
        boolean deleted = specialtyService.deleteSpecialty(id);
        redirectAttrs.addFlashAttribute(deleted ? "successMessage" : "errorMessage",
            deleted ? "Xóa chuyên khoa thành công!" : "Xóa chuyên khoa thất bại!");
        return "redirect:/specialty";
    }

    @GetMapping
    public String listSpecialties(@RequestParam(required = false) Integer page,
                                  @RequestParam(required = false) Integer size,
                                  Model model) {
        Pageable pageable = PaginationUtils.createPageable(page, size, paginationConfig);
        Page<Specialty> specialties = specialtyService.getAllSpecialty(pageable);
        model.addAttribute("specialties", specialties);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", specialties.getTotalPages());
        model.addAttribute("totalElements", specialties.getTotalElements());
        return "specialty/list-specialty";
    }
}
