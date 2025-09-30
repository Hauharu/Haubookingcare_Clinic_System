package com.trunghau1510.Clinic_Management.controller;

import lombok.AccessLevel;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import com.trunghau1510.Clinic_Management.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import com.trunghau1510.Clinic_Management.service.UserService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@ControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IndexController {

    UserService userService;

    @RequestMapping("/")
    public String index(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            User user = userService.getUserByUsername(username);
            if (user != null) {
                model.addAttribute("currentUser", user);
            }
        }
        return "index";
    }
}