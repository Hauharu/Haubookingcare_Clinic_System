package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.dto.response.DoctorResponse;
import lombok.AccessLevel;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import com.trunghau1510.Clinic_Management.entity.enums.Role;
import com.trunghau1510.Clinic_Management.entity.enums.Gender;
import com.trunghau1510.Clinic_Management.service.UserService;
import com.trunghau1510.Clinic_Management.utils.PaginationUtils;
import com.trunghau1510.Clinic_Management.service.DoctorService;
import com.trunghau1510.Clinic_Management.exception.AppException;
import com.trunghau1510.Clinic_Management.config.PaginationConfig;
import com.trunghau1510.Clinic_Management.service.SpecialtyService;
import com.trunghau1510.Clinic_Management.dto.response.UserResponse;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.trunghau1510.Clinic_Management.dto.request.DoctorCreationRequest;
import com.trunghau1510.Clinic_Management.mapper.DoctorMapper;
import com.trunghau1510.Clinic_Management.dto.request.DoctorUpdateRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/doctors")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DoctorController {

    UserService userService;
    DoctorService doctorService;
    SpecialtyService specialtyService;
    PaginationConfig paginationConfig;
    DoctorMapper doctorMapper;

    @GetMapping("/create-doctor")
    public String createDoctorForm(Model model) {
        model.addAttribute("doctor", new DoctorCreationRequest());
        model.addAttribute("specialties", specialtyService.getAllSpecialty());
        model.addAttribute("genders", Gender.values());
        return "doctor/register-doctor";
    }

    @PostMapping("/create-doctor")
    public String createDoctor(@ModelAttribute("doctor") @Valid DoctorCreationRequest request,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("doctor", request);
            model.addAttribute("specialties", specialtyService.getAllSpecialty());
            model.addAttribute("genders", Gender.values());
            return "doctor/register-doctor";
        }

        request.setRole(Role.DOCTOR);
        userService.createUser(request);
        redirectAttributes.addFlashAttribute("successMessage", "Tạo tài khoản bác sĩ thành công!");
        return "redirect:/doctors";
    }

    @GetMapping("/update/{id}")
    public String updateDoctorForm(@PathVariable String id, Model model) {
        DoctorResponse doctorResponse = doctorService.getDoctorById(id);
        DoctorUpdateRequest doctor = doctorMapper.toDoctorUpdateRequest(doctorResponse);
        model.addAttribute("doctor", doctor);
        model.addAttribute("specialties", specialtyService.getAllSpecialty());
        model.addAttribute("genders", Gender.values());
        return "doctor/update-doctor";
    }

    @PostMapping("/update/{id}")
    public String updateDoctor(@PathVariable String id,
                             @ModelAttribute("doctor") @Valid DoctorUpdateRequest request,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctor", request);
            model.addAttribute("specialties", specialtyService.getAllSpecialty());
            model.addAttribute("genders", Gender.values());
            return "doctor/update-doctor";
        }
        try {
            doctorService.updateDoctor(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin bác sĩ thành công!");
            return "redirect:/doctors";
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật thất bại: " + e.getMessage());
            return "redirect:/doctors/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteDoctor(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            doctorService.deleteDoctorById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa bác sĩ thành công!");
        } catch (AppException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/doctors";
    }

    @GetMapping
    public String listDoctors(@RequestParam(required = false) Integer page,
                              @RequestParam(required = false) Integer size,
                              Model model) {
        Pageable pageable = PaginationUtils.createPageable(page, size, paginationConfig);
        Page<UserResponse> doctorUsers = userService.getUsersByRole(Role.DOCTOR, pageable);

        model.addAttribute("doctors", doctorUsers);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", doctorUsers.getTotalPages());
        model.addAttribute("totalElements", doctorUsers.getTotalElements());

        return "doctor/list-doctors";
    }
}