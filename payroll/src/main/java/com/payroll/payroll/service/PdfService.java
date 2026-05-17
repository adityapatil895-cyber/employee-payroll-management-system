package com.payroll.payroll.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.payroll.payroll.model.Employee;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;

@Service
public class PdfService {

    public void generatePayslip(Employee employee, HttpServletResponse response) throws IOException {

        try {

            String fileName = "payslip_" + employee.getName().replaceAll(" ", "_") + ".pdf";


            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();


            Font titleFont   = new Font(Font.HELVETICA, 18, Font.BOLD, Color.WHITE);
            Font headingFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont  = new Font(Font.HELVETICA, 11, Font.NORMAL);
            Font boldFont    = new Font(Font.HELVETICA, 11, Font.BOLD);


            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);

            PdfPCell headerCell = new PdfPCell(new Phrase("Employee Payroll Management System", titleFont));
            headerCell.setBackgroundColor(new Color(33, 37, 41));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(15);
            headerCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(headerCell);

            PdfPCell subCell = new PdfPCell(new Phrase("Monthly Payslip",
                    new Font(Font.HELVETICA, 12, Font.NORMAL, Color.WHITE)));
            subCell.setBackgroundColor(new Color(33, 37, 41));
            subCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            subCell.setPadding(5);
            subCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(subCell);

            document.add(headerTable);
            document.add(Chunk.NEWLINE);

            PdfPTable empTable = new PdfPTable(2);
            empTable.setWidthPercentage(100);
            empTable.setSpacingBefore(10);

            addTableRow(empTable, "Employee Name", employee.getName(), boldFont, normalFont);
            addTableRow(empTable, "Employee ID", String.valueOf(employee.getId()), boldFont, normalFont);
            addTableRow(empTable, "Email", employee.getEmail(), boldFont, normalFont);
            addTableRow(empTable, "Department", employee.getDepartment(), boldFont, normalFont);
            addTableRow(empTable, "Overtime Hours", String.valueOf(employee.getOvertimeHours()), boldFont, normalFont);

            document.add(empTable);
            document.add(Chunk.NEWLINE);


            Paragraph earningsTitle = new Paragraph("Earnings", headingFont);
            earningsTitle.setSpacingBefore(10);
            document.add(earningsTitle);

            PdfPTable earningsTable = new PdfPTable(2);
            earningsTable.setWidthPercentage(100);
            earningsTable.setSpacingBefore(5);

            addTableRow(earningsTable, "Basic Salary",    "₹" + employee.getBasicSalary(), boldFont, normalFont);
            addTableRow(earningsTable, "HRA (40%)",       "₹" + employee.getHra(), boldFont, normalFont);
            addTableRow(earningsTable, "Allowances (20%)","₹" + employee.getAllowances(), boldFont, normalFont);
            addTableRow(earningsTable, "Overtime Pay",    "₹" + employee.getOvertimePay(), boldFont, normalFont);

            PdfPCell grossLabel = new PdfPCell(new Phrase("Gross Salary", boldFont));
            grossLabel.setBackgroundColor(new Color(198, 239, 206));
            grossLabel.setPadding(8);

            PdfPCell grossValue = new PdfPCell(new Phrase("₹" + employee.getGrossSalary(), boldFont));
            grossValue.setBackgroundColor(new Color(198, 239, 206));
            grossValue.setPadding(8);

            earningsTable.addCell(grossLabel);
            earningsTable.addCell(grossValue);

            document.add(earningsTable);
            document.add(Chunk.NEWLINE);


            Paragraph deductionsTitle = new Paragraph("Deductions", headingFont);
            deductionsTitle.setSpacingBefore(10);
            document.add(deductionsTitle);

            PdfPTable deductionsTable = new PdfPTable(2);
            deductionsTable.setWidthPercentage(100);
            deductionsTable.setSpacingBefore(5);

            addTableRow(deductionsTable, "Provident Fund (12%)", "₹" + employee.getPf(), boldFont, normalFont);
            addTableRow(deductionsTable, "Income Tax (10%)",     "₹" + employee.getTax(), boldFont, normalFont);

            PdfPCell dedLabel = new PdfPCell(new Phrase("Total Deductions", boldFont));
            dedLabel.setBackgroundColor(new Color(255, 199, 206));
            dedLabel.setPadding(8);

            PdfPCell dedValue = new PdfPCell(new Phrase("₹" + (employee.getPf() + employee.getTax()), boldFont));
            dedValue.setBackgroundColor(new Color(255, 199, 206));
            dedValue.setPadding(8);

            deductionsTable.addCell(dedLabel);
            deductionsTable.addCell(dedValue);

            document.add(deductionsTable);
            document.add(Chunk.NEWLINE);


            PdfPTable netTable = new PdfPTable(2);
            netTable.setWidthPercentage(100);
            netTable.setSpacingBefore(10);

            Font netFont = new Font(Font.HELVETICA, 13, Font.BOLD, Color.WHITE);

            PdfPCell netLabel = new PdfPCell(new Phrase("NET SALARY", netFont));
            netLabel.setBackgroundColor(new Color(25, 135, 84));
            netLabel.setPadding(12);
            netLabel.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell netValue = new PdfPCell(new Phrase("₹" + employee.getNetSalary(), netFont));
            netValue.setBackgroundColor(new Color(25, 135, 84));
            netValue.setPadding(12);
            netValue.setHorizontalAlignment(Element.ALIGN_CENTER);

            netTable.addCell(netLabel);
            netTable.addCell(netValue);

            document.add(netTable);


            document.close();


            response.flushBuffer();

        } catch (Exception e) {
            e.printStackTrace(); // shows actual error in console
        }
    }


    private void addTableRow(PdfPTable table, String label, String value, Font boldFont, Font normalFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, boldFont));
        labelCell.setPadding(8);
        labelCell.setBackgroundColor(new Color(248, 249, 250));

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "-", normalFont));
        valueCell.setPadding(8);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}