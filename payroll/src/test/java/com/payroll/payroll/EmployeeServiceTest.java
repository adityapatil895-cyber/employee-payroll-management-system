package com.payroll.payroll;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmployeeServiceTest {

    @Test
    void testBasicSalaryCalculation() {
        double basic = 30000;
        double hra = basic * 0.40;
        double pf = basic * 0.12;
        double net = basic + hra - pf;
        assertEquals(38400.0, net);
    }

    @Test
    void testHRACalculation() {
        double basic = 50000;
        double hra = basic * 0.40;
        assertEquals(20000.0, hra);
    }

    @Test
    void testPFDeduction() {
        double basic = 40000;
        double pf = basic * 0.12;
        assertEquals(4800.0, pf);
    }

    @Test
    void testNetSalaryIsPositive() {
        double basic = 25000;
        double hra = basic * 0.40;
        double pf = basic * 0.12;
        double net = basic + hra - pf;
        assertTrue(net > 0);
    }
}