package com.payroll.payroll.service;

import com.payroll.payroll.model.PayrollConfig;
import com.payroll.payroll.repository.PayrollConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayrollConfigService {

    @Autowired
    private PayrollConfigRepository repo;

    public double get(String key, double defaultValue) {
        return repo.findById(key)
                .map(PayrollConfig::getConfigValue)
                .orElse(defaultValue);
    }

    public List<PayrollConfig> getAll() {
        return repo.findAll();
    }

    public void saveAll(double hra, double allowances, double pf, double tax) {
        repo.save(new PayrollConfig("hra", hra));
        repo.save(new PayrollConfig("allowances", allowances));
        repo.save(new PayrollConfig("pf", pf));
        repo.save(new PayrollConfig("tax", tax));
    }
}
