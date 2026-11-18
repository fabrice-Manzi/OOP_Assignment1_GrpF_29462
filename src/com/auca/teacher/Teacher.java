package com.auca.teacher;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Registrable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Teacher extends AcademicMember {
    private String employeeNumber;
    private String specialization;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String officeNumber;
    private String employmentType;

    public Teacher(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth, String memberNumber, Department department, LocalDate joinDate, String academicStatus, String institutionalEmail, String advisorName, String employeeNumber, String specialization, LocalDate hireDate, BigDecimal salary, String officeNumber, String employmentType) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth, memberNumber, department, joinDate, academicStatus, institutionalEmail, advisorName);
        this.employeeNumber = employeeNumber;
        this.specialization = specialization;
        this.hireDate = hireDate;
        this.salary = salary;
        this.officeNumber = officeNumber;
        this.employmentType = employmentType;
    }

    public static List<String> allowedEmployeeTypes() { return List.of("FULL_TIME", "PART_TIME","CONTRACT"); }

    public String getEmployeeNumber() {return employeeNumber;}
    public void setEmployeeNumber(String employeeNumber) {this.employeeNumber = employeeNumber;}

    public String getSpecialization() {return specialization;}
    public void setSpecialization(String specialization) {this.specialization = specialization;}

    public LocalDate getHireDate() {return hireDate;}
    public void setHireDate(LocalDate hireDate) {this.hireDate = hireDate;}

    public BigDecimal getSalary() {return salary;}
    public void setSalary(BigDecimal salary) {this.salary = salary;}

    public String getOfficeNumber() {return officeNumber;}
    public void setOfficeNumber(String officeNumber) {this.officeNumber = officeNumber;}

    public String getEmploymentType() {return employmentType;}
    public void setEmploymentType(String employmentType) {this.employmentType = employmentType;}

    @Override
    public String getAcademicCategory() {return "Faculty";}
    @Override
    public String getRoleDescription() {return "Teacher";}

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Registrable.checkLength(employeeNumber,"Employer Number",MIN_ID_LENGTH,MAX_PHONE_LENGTH);
        Registrable.checkLength(specialization,"Specialization",MIN_NAME_LENGTH,100);
        Registrable.checkNotFutureDates(hireDate,"Hire date");
        if(salary == null || salary.signum() <= 0){
            throw new ValidationException("Salary must be a positive number");
        }
        Registrable.checkLength(officeNumber,"Office Number",1,3);
        if(employmentType == null || !allowedEmployeeTypes().contains(employmentType)){
            throw new ValidationException("Employee Type must be one of "+allowedEmployeeTypes()+".");
        }
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("Employee Number: " + employeeNumber);
        System.out.println("Specialization: " + specialization);
        System.out.println("Hire Date: " + Registrable.formatDate(hireDate));
        System.out.println("Salary: " + salary);
        System.out.println("Office Number: " + officeNumber);
        System.out.println("EmploymentType: " + employmentType);
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + getId() + "] " + getDisplayName() + "-" + employeeNumber + "(teacher specialized in" + specialization + ")" ;
    }

}
