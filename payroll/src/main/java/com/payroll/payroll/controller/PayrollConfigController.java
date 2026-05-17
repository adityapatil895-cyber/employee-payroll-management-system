package com.payroll.payroll.controller;

import com.payroll.payroll.service.PayrollConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PayrollConfigController {

    @Autowired
    private PayrollConfigService configService;

    @GetMapping("/payroll/config")
    public String showConfig(Model model) {
        model.addAttribute("hra", configService.get("hra", 40.0));
        model.addAttribute("allowances", configService.get("allowances", 20.0));
        model.addAttribute("pf", configService.get("pf", 12.0));
        model.addAttribute("tax", configService.get("tax", 10.0));
        return "payroll/config";
    }

    @PostMapping("/payroll/config/save")
    public String saveConfig(@RequestParam double hra,
                             @RequestParam double allowances,
                             @RequestParam double pf,
                             @RequestParam double tax) {
        configService.saveAll(hra, allowances, pf, tax);
        return "redirect:/payroll/config?success";
    }
}