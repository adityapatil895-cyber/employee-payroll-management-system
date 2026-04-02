package com.payroll.payroll.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class homecontroller {
    @GetMapping("/")
    public String home() {
        return "home";
    }
}

