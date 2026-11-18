package com.auca.administrator;

import com.auca.abstracts.Person;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Registrable;

import java.time.LocalDate;

public class Administrator extends Person  {
    public static final int MAX_ACCESS_LEVEL = 5;

    private String staffNumber;
    private String jobTitle;
    private String assignedOffice;
    private LocalDate employmentDate;
    private int accessLevel;
    private boolean active;

    public Administrator(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth, String staffNumber, String jobTitle, String assignedOffice, LocalDate employmentDate, int accessLevel, boolean active) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.staffNumber = staffNumber;
        this.jobTitle = jobTitle;
        this.assignedOffice = assignedOffice;
        this.employmentDate = employmentDate;
        this.accessLevel = accessLevel;
        this.active = active;
    }

    public static int maxAccessLevel() {return MAX_ACCESS_LEVEL;}

    public String getStaffNumber() {return staffNumber;}
    public void setStaffNumber(String staffNumber) {this.staffNumber = staffNumber;}

    public String getJobTitle() {return jobTitle;}
    public void setJobTitle(String jobTitle) {this.jobTitle = jobTitle;}

    public String getAssignedOffice() {return assignedOffice;}
    public void setAssignedOffice(String assignedOffice) {this.assignedOffice = assignedOffice;}

    public LocalDate getEmploymentDate() {return employmentDate;}
    public void setEmploymentDate(LocalDate employmentDate) {this.employmentDate = employmentDate;}

    public int getAccessLevel() {return accessLevel;}
    public void setAccessLevel(int accessLevel) {this.accessLevel = accessLevel;}

    public void setActive(boolean active) {this.active = active;}

    @Override
    public String getRoleDescription() {return "Administrator";}
    @Override
    public boolean isActive() {return active;}

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Registrable.checkLength(staffNumber,"Staff Number",MIN_ID_LENGTH,MAX_ID_LENGTH);
        Registrable.checkLength(jobTitle,"Job Title",MIN_NAME_LENGTH,100);
        Registrable.checkLength(assignedOffice,"Assigned Office",1,60);
        Registrable.checkNotFutureDates(employmentDate,"Employment Date");
        if(accessLevel < 1 || accessLevel > MAX_ACCESS_LEVEL){
            throw new ValidationException("Access level must be between 1 and "+ MAX_ACCESS_LEVEL);
        }
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("========== STAFF MEMBER DETAILS ============");
        System.out.println("Staff Number: " + staffNumber);
        System.out.println("Job Title: " + jobTitle);
        System.out.println("Assigned Office: " + assignedOffice);
        System.out.println("Employment Date: " + Registrable.formatDate(employmentDate));
        System.out.println("Access Level: " + accessLevel);
        System.out.println("Active: " + (active?"Yes":"No"));
    }

    @Override
    public String getRegistrationSummary() {
        return  "[" + getId() + "] " + getDisplayName() + " - " + staffNumber + " (Administrator acting as " + jobTitle + ")";
    }
}
