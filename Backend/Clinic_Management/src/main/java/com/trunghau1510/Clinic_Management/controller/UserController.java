package com.trunghau1510.Clinic_Management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/login")
    public String loginView() {
        return "authentication/login";
    }
}




