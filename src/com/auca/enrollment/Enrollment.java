package com.auca.enrollment;

import com.auca.course.Course;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;
import java.time.LocalDate;
import java.util.List;

public class Enrollment implements Identifiable, Registrable {
    private String enrollmentId;
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private String semester;
    private String enrollmentStatus;

    public Enrollment(String enrollmentId, Student student, Course course, LocalDate enrollmentDate, String semester, String enrollmentStatus) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.semester = semester;
        this.enrollmentStatus = enrollmentStatus;
    }
    public static List<String> allowedSemester() {return List.of("First","Second","Summer");}
    public static List<String> allowedStatuses() {return List.of("COMPLETED","ACTIVE","DROPPED","SUSPENDED");}

    public String getEnrollmentId() {return enrollmentId;}
    public void setEnrollmentId(String enrollmentId) {this.enrollmentId = enrollmentId;}

    public Student getStudent() {return student;}
    public void setStudent(Student student) {this.student = student;}

    public Course getCourse() {return course;}
    public void setCourse(Course course) {this.course = course;}

    public LocalDate getEnrollmentDate() {return enrollmentDate;}
    public void setEnrollmentDate(LocalDate enrollmentDate) {this.enrollmentDate = enrollmentDate;}

    public String getSemester() {return semester;}
    public void setSemester(String semester) {this.semester = semester;}

    public String getEnrollmentStatus() {return enrollmentStatus;}
    public void setEnrollmentStatus(String enrollmentStatus) {this.enrollmentStatus = enrollmentStatus;}

    @Override
    public String getId() {return enrollmentId;}
    @Override
    public String getDisplayName() {return student.getDisplayName()+ " in "+ course.getCourseTitle();}
    @Override
    public boolean isActive() {return DEFAULT_STATUS.equalsIgnoreCase(enrollmentStatus);}

    @Override
    public void validate() throws ValidationException {
        Registrable.checkLength(enrollmentId,"Enrollment ID",MIN_ID_LENGTH,MAX_ID_LENGTH);
        Registrable.checkNotNull(student,"Student");
        Registrable.checkNotNull(course,"Course");
        Registrable.checkNotFutureDates(enrollmentDate,"Enrollment Date");
        if (semester == null || !allowedSemester().contains(semester)) {
            throw new ValidationException("Semester must be one of " + allowedSemester() + ".");
        }
        if (enrollmentStatus == null || !allowedStatuses().contains(enrollmentStatus)) {
            throw new ValidationException("Enrollment status must be one of " + allowedStatuses() + ".");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("--- ENROLLMENT ---");
        System.out.println("  Enrollment ID : " + enrollmentId);
        System.out.println("  Student       : " + student.getRegistrationSummary());
        System.out.println("  Course        : " + course.getRegistrationSummary());
        System.out.println("  Date          : " + Registrable.formatDate(enrollmentDate));
        System.out.println("  Semester      : " + semester);
        System.out.println("  Status        : " + enrollmentStatus);

    }

    @Override
    public String getRegistrationSummary() {
        return "[" + enrollmentId + "] " + student.getDisplayName() + " -> " + course.getCourseCode()
            + " (" + semester + ", " + enrollmentStatus + ")";
        }
}
