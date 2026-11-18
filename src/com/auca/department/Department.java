package com.auca.department;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Department implements Identifiable, Registrable {
    private  final String departmentId;
    private String departmentName;
    private String departmentCode;
    private String officeLocation;
    private String contactEmail;
    private Teacher headTeacher;

    public Department(String departmentId, String departmentName, String departmentCode, String officeLocation, String contactEmail, Teacher headTeacher) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.officeLocation = officeLocation;
        this.contactEmail = contactEmail;
        this.headTeacher = headTeacher;
    }

    public Department(String departmentId, String departmentName, String departmentCode, String officeLocation, String contactEmail){
        this(departmentId,departmentName,departmentCode,officeLocation,contactEmail,null);
    }

    public String getDepartmentId() {return departmentId;}

    public String getDepartmentName() {return departmentName;}
    public void setDepartmentName(String departmentName) {this.departmentName = departmentName;}

    public String getDepartmentCode() {return departmentCode;}
    public void setDepartmentCode(String departmentCode) {this.departmentCode = departmentCode;}

    public String getOfficeLocation() {return officeLocation;}
    public void setOfficeLocation(String officeLocation) {this.officeLocation = officeLocation;}

    public String getContactEmail() {return contactEmail;}
    public void setContactEmail(String contactEmail) {this.contactEmail = contactEmail;}

    public Teacher getHeadTeacher() {return headTeacher;}
    public void setHeadTeacher(Teacher headTeacher) {this.headTeacher = headTeacher;}

    @Override
    public String getId() {return departmentId;}
    @Override
    public String getDisplayName() {return departmentName;}
    @Override
    public boolean isActive() {return true;}

    @Override
    public void validate() throws ValidationException {
        Registrable.checkLength(departmentId,"Department ID",MIN_ID_LENGTH,MAX_ID_LENGTH);
        Registrable.checkLength(departmentName,"Department Name",MIN_NAME_LENGTH,MAX_NAME_LENGTH);
        Registrable.checkLength(departmentCode,"Department code",2,5);
        Registrable.checkLength(officeLocation,"OfficeLocation",2,50);
        Registrable.checkEmail(contactEmail,"Contact Email");
        if(headTeacher != null && headTeacher.getDepartment() !=this){
            throw new ValidationException("Head Teacher must belong to this department");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========= DEPARTMENT DETAILS ========");
        System.out.println("Department ID: "+departmentId);
        System.out.println("Name: "+departmentName);
        System.out.println("Department Code: "+departmentCode);
        System.out.println("Office Location: "+officeLocation);
        System.out.println("Contact Email: "+contactEmail);
        System.out.println("Head Teacher: "+(headTeacher==null ?"none":headTeacher.getDisplayName()));
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + departmentId + "] " + departmentName + "(" + departmentCode +")"; }
}
