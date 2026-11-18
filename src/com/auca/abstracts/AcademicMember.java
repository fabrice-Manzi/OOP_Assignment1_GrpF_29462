package com.auca.abstracts;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;
import java.util.List;

public abstract class AcademicMember extends Person {
    private String memberNumber;
    private Department department;
    private LocalDate joinDate;
    private String academicStatus;
    private String institutionalEmail;
    private String advisorName;

    public AcademicMember(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth, String memberNumber, Department department, LocalDate joinDate, String academicStatus, String institutionalEmail, String advisorName) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.memberNumber = memberNumber;
        this.department = department;
        this.joinDate = joinDate;
        this.academicStatus = academicStatus;
        this.institutionalEmail = institutionalEmail;
        this.advisorName = advisorName;
    }

    public static List<String> allowedStatuses() {return List.of("ACTIVE","INACTIVE");}

    public String getMemberNumber() {return memberNumber;}
    public void setMemberNumber(String memberNumber) {this.memberNumber = memberNumber;}

    public Department getDepartment() {return department;}
    public void setDepartment(Department department) {this.department = department;}

    public LocalDate getJoinDate() {return joinDate;}
    public void setJoinDate(LocalDate joinDate) {this.joinDate = joinDate;}

    public String getAcademicStatus() {return academicStatus;}
    public void setAcademicStatus(String academicStatus) {this.academicStatus = academicStatus;}

    public String getInstitutionalEmail() {return institutionalEmail;}
    public void setInstitutionalEmail(String institutionalEmail) {this.institutionalEmail = institutionalEmail;}

    public String getAdvisorName() {return advisorName;}
    public void setAdvisorName(String advisorName) {this.advisorName = advisorName;}

    public abstract String getAcademicCategory();


    @Override
    public boolean isActive() {
        return DEFAULT_STATUS.equalsIgnoreCase(academicStatus);
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Registrable.checkLength(memberNumber,"Member Number",MIN_ID_LENGTH,MAX_ID_LENGTH);
        Registrable.checkNotNull(department,"Department");
        Registrable.checkNotFutureDates(joinDate,"Join Date");
        if(academicStatus==null || !allowedStatuses().contains(academicStatus)){
            throw new ValidationException("Academic Status must be in [" + allowedStatuses() + "]");
        }
        Registrable.checkEmail(institutionalEmail,"Institutional Email");
        Registrable.checkLength(advisorName,"Advisor Name",MIN_ID_LENGTH,MAX_ID_LENGTH);

    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("Member Number: " + memberNumber);
        System.out.println("Department: " + (department==null ?"-" :department.getRegistrationSummary()));
        System.out.println("Join Date: " + Registrable.formatDate(joinDate));
        System.out.println("Status: " + academicStatus);
        System.out.println("Institutional Email: " + institutionalEmail);
        System.out.println("Advisor Name: " + advisorName);
        System.out.println("category: " + getAcademicCategory());
    }


}

