package com.auca.librarybook;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

public class LibraryBook implements Identifiable, Registrable {
    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;

    public LibraryBook(String bookId, String isbn, String title, String author, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public String getBookId() {return bookId;}
    public void setBookId(String bookId) {this.bookId = bookId;}

    public String getIsbn() {return isbn;}
    public void setIsbn(String isbn) {this.isbn = isbn;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getAuthor() {return author;}
    public void setAuthor(String author) {this.author = author;}

    public int getTotalCopies() {return totalCopies;}
    public void setTotalCopies(int totalCopies) {this.totalCopies = totalCopies;}

    public int getAvailableCopies() {return availableCopies;}
    public void setAvailableCopies(int availableCopies) {this.availableCopies = availableCopies;}

    @Override
    public String getId() {return bookId;}
    @Override
    public String getDisplayName() {return title;}
    @Override
    public boolean isActive() {return availableCopies > 0;}

    @Override
    public void validate() throws ValidationException {
        Registrable.checkLength(bookId,"Book ID",MIN_ID_LENGTH,MAX_ID_LENGTH);
        Registrable.checkText(isbn,"ISBN");
        if (totalCopies < 0) throw new ValidationException("Total copies cannot be negative.");
        Registrable.checkLength(title,"Title",1,100);
        Registrable.checkLength(author,"Author",MIN_NAME_LENGTH,MAX_NAME_LENGTH);
        if (availableCopies < 0) throw new ValidationException("Available copies cannot be negative.");
        if (availableCopies > totalCopies) {
            throw new ValidationException("Available copies (" + availableCopies + ") cannot exceed total copies (" + totalCopies + ").");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("--- LIBRARY BOOK ---");
        System.out.println("  Book ID       : " + bookId);
        System.out.println("  ISBN          : " + isbn);
        System.out.println("  Title         : " + title);
        System.out.println("  Author        : " + author);
        System.out.println("  Copies        : " + availableCopies + " available of " + totalCopies);
    }

    @Override
    public String getRegistrationSummary() {return "[" + bookId + "] \"" + title + "\" by " + author + " (" + availableCopies + "/" + totalCopies + " available)";}
}
