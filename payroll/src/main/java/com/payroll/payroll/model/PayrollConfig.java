package com.payroll.payroll.model;

import jakarta.persistence.*;

@Entity
public class PayrollConfig {

    @Id
    private String configKey;

    private double configValue;

    public PayrollConfig() {}

    public PayrollConfig(String configKey, double configValue) {
        this.configKey = configKey;
        this.configValue = configValue;
    }

    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }

    public double getConfigValue() { return configValue; }
    public void setConfigValue(double configValue) { this.configValue = configValue; }
}