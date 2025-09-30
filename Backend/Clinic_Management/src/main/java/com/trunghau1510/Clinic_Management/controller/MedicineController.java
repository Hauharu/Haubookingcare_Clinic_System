package com.trunghau1510.Clinic_Management.controller;

import jakarta.validation.Valid;
import lombok.experimental.FieldDefaults;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import com.trunghau1510.Clinic_Management.utils.PaginationUtils;
import com.trunghau1510.Clinic_Management.config.PaginationConfig;
import com.trunghau1510.Clinic_Management.service.MedicineService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.trunghau1510.Clinic_Management.dto.response.MedicineResponse;
import com.trunghau1510.Clinic_Management.dto.request.MedicineUpdateRequest;
import com.trunghau1510.Clinic_Management.dto.request.MedicineCreationRequest;
import com.trunghau1510.Clinic_Management.mapper.MedicineMapper;

@Controller
@RequiredArgsConstructor
@RequestMapping("/medicines")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class MedicineController {

    MedicineService medicineService;
    PaginationConfig paginationConfig;
    MedicineMapper medicineMapper;

    @GetMapping("/create")
    public String createMedicineForm(Model model) {
        model.addAttribute("medicine", new MedicineCreationRequest());
        return "medicine/create-medicine";
    }

    @PostMapping("/create")
    public String createMedicine(@ModelAttribute("medicine") @Valid MedicineCreationRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "medicine/create-medicine";
        }
        medicineService.createMedicine(request);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm thuốc mới thành công!");
        return "redirect:/medicines";
    }

    @GetMapping("/update/{id}")
    public String updateMedicineForm(@PathVariable String id, Model model) {
        MedicineResponse medicine = medicineService.getMedicineById(id);
        MedicineUpdateRequest updateRequest = medicineMapper.toMedicineUpdateRequest(medicine);
        model.addAttribute("medicine", updateRequest);
        return "medicine/create-medicine";
    }

    @PostMapping("/update/{id}")
    public String updateMedicine(@PathVariable String id,
                                 @ModelAttribute("medicine") @Valid MedicineUpdateRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "medicine/create-medicine";
        }
        request.setId(id);
        medicineService.updateMedicine(request);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thuốc thành công!");
        return "redirect:/medicines";
    }

    @PostMapping("/delete/{id}")
    public String deleteMedicine(@PathVariable String id, RedirectAttributes redirectAttributes) {
        medicineService.deleteMedicine(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa thuốc thành công!");
        return "redirect:/medicines";
    }

    @GetMapping
    public String listMedicines(@RequestParam(required = false) Integer page,
                                @RequestParam(required = false) Integer size,
                                Model model) {
        Pageable pageable = PaginationUtils.createPageable(page, size, paginationConfig);
        Page<MedicineResponse> medicines = medicineService.getAllMedicines(pageable);
        model.addAttribute("medicines", medicines);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", medicines.getTotalPages());
        model.addAttribute("totalElements", medicines.getTotalElements());
        return "medicine/list-medicine";
    }
}
