package com.auca.assessment;

import com.auca.enrollment.Enrollment;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;
import java.util.List;

public class Assessment implements Identifiable, Registrable {
    private String assessmentId;
    private Enrollment enrollment;
    private String assessmentType;
    private double maximumScore;
    private double obtainedScore;
    private LocalDate assessmentDate;

    public Assessment(String assessmentId, Enrollment enrollment, String assessmentType, double maximumScore, double obtainedScore, LocalDate assessmentDate) {
        this.assessmentId = assessmentId;
        this.enrollment = enrollment;
        this.assessmentType = assessmentType;
        this.maximumScore = maximumScore;
        this.obtainedScore = obtainedScore;
        this.assessmentDate = assessmentDate;
    }

    public static List<String> allowedAssessmentTypes() {return List.of("QUIZ","ASSIGNMENT","MID-EXAM","FINAL EXAM");}

    public String getAssessmentId() {return assessmentId;}
    public void setAssessmentId(String assessmentId) {this.assessmentId = assessmentId;}

    public Enrollment getEnrollment() {return enrollment;}
    public void setEnrollment(Enrollment enrollment) {this.enrollment = enrollment;}

    public String getAssessmentType() {return assessmentType;}
    public void setAssessmentType(String assessmentType) {this.assessmentType = assessmentType;}

    public double getMaximumScore() {return maximumScore;}
    public void setMaximumScore(double maximumScore) {this.maximumScore = maximumScore;}

    public double getObtainedScore() {return obtainedScore;}
    public void setObtainedScore(double obtainedScore) {this.obtainedScore = obtainedScore;}

    public LocalDate getAssessmentDate() {return assessmentDate;}
    public void setAssessmentDate(LocalDate assessmentDate) {this.assessmentDate = assessmentDate;}

    public double getPercentage() {return maximumScore ==0 ? 0 :obtainedScore * 100.0/maximumScore;}

    @Override
    public String getId() {return assessmentId;}
    @Override
    public String getDisplayName() {return assessmentType + "_"+ enrollment.getDisplayName();}
    @Override
    public boolean isActive() {return enrollment.isActive();}

    @Override
    public void validate() throws ValidationException {
        Registrable.checkLength(assessmentId, "Assessment ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Registrable.checkNotNull(enrollment, "Enrollment");
        if (assessmentType == null || !allowedAssessmentTypes().contains(assessmentType)) {
            throw new ValidationException("Assessment Type must be one of " + allowedAssessmentTypes());
        }
        if (!(maximumScore > 0)) throw new ValidationException("Maximum Score must be greater than 0");{
            if (obtainedScore < 0 || obtainedScore > maximumScore) {
                throw new ValidationException("Obtained score must be between 0 and the maximum score (" + maximumScore + ").");
            }
        }
        Registrable.checkNotFutureDates(assessmentDate, "Assessment Date");
    }

    @Override
    public void printDetails() {
        System.out.println("--- ASSESSMENT ---");
        System.out.println("  Assessment ID : " + assessmentId);
        System.out.println("  Enrollment    : " + enrollment.getRegistrationSummary());
        System.out.println("  Type          : " + assessmentType);
        System.out.println("  Score         : " + obtainedScore + "/" + maximumScore +"(" + String.format(" (%.1f%%)", getPercentage())+")" );
        System.out.println("  Date          : " + Registrable.formatDate(assessmentDate));

    }

    @Override
    public String getRegistrationSummary() {return "[" + assessmentId +"]"+ assessmentType + " " + obtainedScore + "/" + maximumScore
            + " - " + enrollment.getStudent().getDisplayName() + " (" + enrollment.getCourse().getCourseCode() + ")";
    }
}
