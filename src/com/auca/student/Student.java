package com.auca.student;

import com.auca.abstracts.AcademicMember;
import com.auca.abstracts.Person;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;

public class Student extends AcademicMember {
    private static final int MAX_STUDY_YEAR=6;
    private String studentNumber;
    private String programName;
    private LocalDate admissionDate;
    private int studyYear;
    private String guardianPhone;
    private boolean scholarshipStatus;

    public Student(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth, String memberNumber, Department department, LocalDate joinDate, String academicStatus, String institutionalEmail, String advisorName, String studentNumber, String programName, LocalDate admissionDate, int studyYear, String guardianPhone, boolean scholarshipStatus) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth, memberNumber, department, joinDate, academicStatus, institutionalEmail, advisorName);
        this.studentNumber = studentNumber;
        this.programName = programName;
        this.admissionDate = admissionDate;
        this.studyYear = studyYear;
        this.guardianPhone = guardianPhone;
        this.scholarshipStatus = scholarshipStatus;
    }

    public static int maxStudyYear(){return MAX_STUDY_YEAR;}

    public String getStudentNumber() {return studentNumber;}
    public void setStudentNumber(String studentNumber) {this.studentNumber = studentNumber;}

    public String getProgramName() {return programName;}
    public void setProgramName(String programName) {this.programName = programName;}

    public LocalDate getAdmissionDate() {return admissionDate;}
    public void setAdmissionDate(LocalDate admissionDate) {this.admissionDate = admissionDate;}

    public int getStudyYear() {return studyYear;}
    public void setStudyYear(int studyYear) {this.studyYear = studyYear;}

    public String getGuardianPhone() {return guardianPhone;}
    public void setGuardianPhone(String guardianPhone) {this.guardianPhone = guardianPhone;}

    public boolean isScholarshipStatus() {return scholarshipStatus;}
    public void setScholarshipStatus(boolean scholarshipStatus) {this.scholarshipStatus = scholarshipStatus;}

    @Override
    public String getAcademicCategory() {return "Learner";}
    @Override
    public String getRoleDescription() {return "Student";}

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Registrable.checkLength(studentNumber,"student Number",MIN_ID_LENGTH,MAX_ID_LENGTH);
        Registrable.checkLength(programName,"program name",MIN_NAME_LENGTH,MAX_NAME_LENGTH);
        Registrable.checkNotFutureDates(admissionDate,"Admission Date");
        if( studyYear < 1 || studyYear > MAX_STUDY_YEAR){
            throw new ValidationException("Study Years must be between 1 and " + MAX_STUDY_YEAR);
        }
        Registrable.checkPhone(guardianPhone,"Guardian Phone");
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("=============STUDENT DETAILS===============");
        System.out.println("Student Number: " + studentNumber);
        System.out.println("Program Name: " + programName);
        System.out.println("Admission Date: " + Registrable.formatDate(admissionDate));
        System.out.println("Study Year: " + studyYear);
        System.out.println("Guardian Phone: " + guardianPhone);
        System.out.println("Scholarship: " + (scholarshipStatus ? "Yes" : "No"));
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + getId() + "] "+ getDisplayName()+"-"+ studentNumber+"(student in "+ programName +")";
    }
}
