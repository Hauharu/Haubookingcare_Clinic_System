package com.trunghau1510.Clinic_Management.controller;

import com.trunghau1510.Clinic_Management.dto.response.DoctorLicenseResponse;
import com.trunghau1510.Clinic_Management.service.DoctorLicenseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/license")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DoctorLicenseController {

    DoctorLicenseService doctorLicenseService;

    @GetMapping
    public String license(Model model, @RequestParam(required = false) Boolean isVerified) {
        List<DoctorLicenseResponse> licenses;
        if (isVerified == null) {
            licenses = doctorLicenseService.getAllLicenses();
        } else if (isVerified) {
            licenses = doctorLicenseService.getAllVerifiedLicenses();
        } else {
            licenses = doctorLicenseService.getAllUnverifiedLicenses();
        }
        model.addAttribute("license", licenses != null ? licenses : new ArrayList<>());
        return "doctor/license";
    }

    @PostMapping("/{licenseId}")
    public String approveLicense(@PathVariable("licenseId") String id, @RequestParam(required = false) Boolean isVerified, RedirectAttributes redirectAttributes) {
        try {
            if (isVerified != null && isVerified) {
                doctorLicenseService.approveLicense(id, true);
                redirectAttributes.addFlashAttribute("successMessage", "Duyệt chứng chỉ hành nghề thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi duyệt chứng chỉ: " + e.getMessage());
        }
        return "redirect:/license";
    }

    @PostMapping(value = "/{licenseId}", params = "_method=delete")
    public String deleteLicense(@PathVariable("licenseId") String id, RedirectAttributes redirectAttributes) {
        try {
            doctorLicenseService.deleteLicense(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa chứng chỉ hành nghề thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa chứng chỉ hành nghề: " + e.getMessage());
        }
        return "redirect:/license";
    }
}
