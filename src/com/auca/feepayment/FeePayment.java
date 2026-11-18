package com.auca.feepayment;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FeePayment implements Identifiable, Registrable {
    private String paymentId;
    private Student student;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String receiptNumber;

    public FeePayment(String paymentId, Student student, BigDecimal amount, LocalDate paymentDate, String paymentMethod, String receiptNumber) {
        this.paymentId = paymentId;
        this.student = student;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
    }
    public static List<String> allowedMethods() {return List.of("CASH","Mobile_Money","BANK","CARD");}

    public String getPaymentId() {return paymentId;}
    public void setPaymentId(String paymentId) {this.paymentId = paymentId;}

    public Student getStudent() {return student;}
    public void setStudent(Student student) {this.student = student;}

    public BigDecimal getAmount() {return amount;}
    public void setAmount(BigDecimal amount) {this.amount = amount;}

    public LocalDate getPaymentDate() {return paymentDate;}
    public void setPaymentDate(LocalDate paymentDate) {this.paymentDate = paymentDate;}

    public String getPaymentMethod() {return paymentMethod;}
    public void setPaymentMethod(String paymentMethod) {this.paymentMethod = paymentMethod;}

    public String getReceiptNumber() {return receiptNumber;}
    public void setReceiptNumber(String receiptNumber) {this.receiptNumber = receiptNumber;}

    @Override
    public String getId() {return paymentId;}
    @Override
    public String getDisplayName() {return "Receipt:"+ receiptNumber;}
    @Override
    public boolean isActive() {return true;}

    @Override
    public void validate() throws ValidationException {
        Registrable.checkLength(paymentId,"Payment ID",MIN_ID_LENGTH,MAX_ID_LENGTH);
        Registrable.checkNotNull(student,"Student");
        if (amount == null || amount.signum() <= 0) throw new ValidationException("Amount must be strictly positive.");
        Registrable.checkNotFutureDates(paymentDate,"Payment Date");
        if (paymentMethod == null || !allowedMethods().contains(paymentMethod)) {
            throw new ValidationException("Payment method must be one of " + allowedMethods() + ".");
        }
        Registrable.checkLength(receiptNumber,"Receipt Number",1,30);
    }

    @Override
    public void printDetails() {
        System.out.println("--- FEE PAYMENT ---");
        System.out.println("  Payment ID    : " + paymentId);
        System.out.println("  Student       : " + student.getRegistrationSummary());
        System.out.println("  Amount        : " + amount);
        System.out.println("  Date          : " + Registrable.formatDate(paymentDate));
        System.out.println("  Method        : " + paymentMethod);
        System.out.println("  Receipt no.   : " + receiptNumber);
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + paymentId + "] " + student.getDisplayName() + " paid " + amount + " on " + Registrable.formatDate(paymentDate) + " (" + paymentMethod + ", " + receiptNumber + ")";
        }
}
