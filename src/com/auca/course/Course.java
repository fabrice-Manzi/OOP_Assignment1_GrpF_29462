package com.auca.course;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Course implements Identifiable, Registrable  {
    private static final int MAX_CREDIT=6;
    private String courseId;
    private  String courseCode;
    private String courseTitle;
    private int creditHours;
    private Department department;
    private Teacher assignedteacher;

    public Course(String courseId, String courseCode, String courseTitle, int creditHours, Department department, Teacher assignedteacher) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditHours = creditHours;
        this.department = department;
        this.assignedteacher = assignedteacher;
    }
    public static int maxCredit() {return MAX_CREDIT;}

    public String getCourseId() {return courseId;}
    public void setCourseId(String courseId) {this.courseId = courseId;}

    public String getCourseCode() {return courseCode;}
    public void setCourseCode(String courseCode) {this.courseCode = courseCode;}

    public String getCourseTitle() {return courseTitle;}
    public void setCourseTitle(String courseTitle) {this.courseTitle = courseTitle;}

    public int getCreditHours() {return creditHours;}
    public void setCreditHours(int creditHours) {this.creditHours = creditHours;}

    public Department getDepartment() {return department;}
    public void setDepartment(Department department) {this.department = department;}

    public Teacher getAssignedteacher() {return assignedteacher;}
    public void setAssignedteacher(Teacher assignedteacher) {this.assignedteacher = assignedteacher;}

    @Override
    public String getId() {return courseId;}
    @Override
    public String getDisplayName() {return courseCode+":  "+courseTitle;}
    @Override
    public boolean isActive() {return true;}

    @Override
    public void validate() throws ValidationException {
        Registrable.checkLength(courseId,"Course ID",MIN_ID_LENGTH,MAX_ID_LENGTH);
        Registrable.checkLength(courseCode,"Course Code",2,10);
        Registrable.checkLength(courseTitle,"Course Code",MIN_ID_LENGTH,MAX_ID_LENGTH);
        if (creditHours < 1 || creditHours > MAX_CREDIT) {
            throw new ValidationException("Credit hours must be between 1 and " + MAX_CREDIT);
        }
        Registrable.checkNotNull(department,"Department");
        Registrable.checkNotNull(assignedteacher,"Assigned Teacher");
    }

    @Override
    public void printDetails() {
        System.out.println("--- COURSE ---");
        System.out.println("  Course ID     : " + courseId);
        System.out.println("  Code          : " + courseCode);
        System.out.println("  Title         : " + courseTitle);
        System.out.println("  Credit hours  : " + creditHours);
        System.out.println("  Department    : " + (department == null ? "-" : department.getRegistrationSummary()));
        System.out.println("  Teacher       : " + (assignedteacher == null ? "-" : assignedteacher.getDisplayName()));
    }

    @Override
    public String getRegistrationSummary() {return "[" + courseId + "] " + courseCode + " - " + courseTitle + " (" + creditHours + " credits)";}
}
