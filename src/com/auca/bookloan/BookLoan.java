package com.auca.bookloan;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import java.time.LocalDate;

public class BookLoan  implements Identifiable, Registrable {
    private String loanId;
    private Student student;
    private LibraryBook book;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;

    public BookLoan(String loanId, Student student, LibraryBook book, LocalDate borrowedDate, LocalDate dueDate, LocalDate returnedDate) {
        this.loanId = loanId;
        this.student = student;
        this.book = book;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
    }

    public BookLoan(String loanId, Student student, LibraryBook book, LocalDate borrowedDate, LocalDate dueDate) {
        this(loanId, student, book, borrowedDate, dueDate, null);
    }

    public String getLoanId() {return loanId;}
    public void setLoanId(String loanId) {this.loanId = loanId;}

    public Student getStudent() {return student;}
    public void setStudent(Student student) {this.student = student;}

    public LibraryBook getBook() {return book;}
    public void setBook(LibraryBook book) {this.book = book;}

    public LocalDate getBorrowedDate() {return borrowedDate;}
    public void setBorrowedDate(LocalDate borrowedDate) {this.borrowedDate = borrowedDate;}

    public LocalDate getDueDate() {return dueDate;}
    public void setDueDate(LocalDate dueDate) {this.dueDate = dueDate;}

    public LocalDate getReturnedDate() {return returnedDate;}
    public void setReturnedDate(LocalDate returnedDate) {this.returnedDate = returnedDate;}

    @Override
    public String getId() {return loanId;}
    @Override
    public String getDisplayName() {return student.getDisplayName()+ "/" + book.getTitle();}
    @Override
    public boolean isActive() {return returnedDate==null;}

    @Override
    public void validate() throws ValidationException {
        Registrable.checkLength(loanId,"Loan ID",MIN_ID_LENGTH,MAX_ID_LENGTH);
        Registrable.checkNotNull(student,"Student");
        Registrable.checkNotNull(book,"Library Book");
        Registrable.checkNotFutureDates(borrowedDate,"Borrowed date");
        Registrable.checkNotNull(dueDate,"Due date");
        if(!dueDate.isAfter(borrowedDate)) throw new ValidationException("Due date must be after borrowed date");
        if(returnedDate != null && returnedDate.isBefore(borrowedDate)) {
            throw new ValidationException("Returned Date must not be before borrowed date");
        }

    }

    @Override
    public void printDetails() {
        System.out.println("--- BOOK LOAN ---");
        System.out.println("  Loan ID       : " + loanId);
        System.out.println("  Student       : " + student.getDisplayName());
        System.out.println("  Book          : " + book.getRegistrationSummary());
        System.out.println("  Borrowed      : " + Registrable.formatDate(borrowedDate));
        System.out.println("  Due           : " + Registrable.formatDate(dueDate));
        System.out.println("  Returned      : " + (returnedDate==null? "(not yet)" : Registrable.formatDate(returnedDate)));
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + loanId + "] " + student.getDisplayName() + " borrowed \"" + book.getTitle() + "\" on " + Registrable.formatDate(borrowedDate) + ", due " + Registrable.formatDate(dueDate)
            + (returnedDate == null ? " (ON LOAN)" : ", returned " + Registrable.formatDate(returnedDate));}
}
