package com.auca.interfaces;

import com.auca.exceptions.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public interface Registrable {
    static final int MIN_NAME_LENGTH = 2;
    static final int MAX_NAME_LENGTH = 60;
    static final int MAX_EMAIL_LENGTH = 100;
    static final int MIN_PHONE_LENGTH = 10;
    static final int MAX_PHONE_LENGTH = 15;
    static final String DATE_PATTERN = "dd/MM/yyyy";

    void validate() throws ValidationException;
    void printDetails();
    String getRegistrationSummary();

    static void checkText(String value, String field) throws ValidationException {
        if(value==null || value.trim().isEmpty()){
            throw new ValidationException(field + " must not be empty");
        }
    }
    static void checkLength(String value, String field,int min,int max) throws ValidationException {
        checkText(value,field);
        int n =value.trim().length();
        if(n<min || n>max){
            throw new ValidationException(field + " must be between " + min + " and " + max + " characters(you typed only "+ n);
        }
    }

    static void checkEmail(String value, String field) throws ValidationException {
        checkText(value, field);
        String email = value.trim();
        if (email.length() > MAX_EMAIL_LENGTH) {
            throw new ValidationException(field + " is too long(must not exceed " + MAX_EMAIL_LENGTH + " characters)");
        }
        int at = email.indexOf('@');
        int dot = email.lastIndexOf('.');
        if (at < 1
                || at != email.lastIndexOf('@')
                || dot < at + 2
                || dot == email.length() - 1
                || email.contains(" ")) {
            throw new ValidationException(field + "contains invalid email address (example:name@auca.ac.rw)");
        }
    }

    static void checkPhone(String value,String field)throws ValidationException{
        checkText(value, field);
        String phone = value.trim();
        if(!phone.matches("\\+?\\d{" + MIN_PHONE_LENGTH + "," + MAX_PHONE_LENGTH + "}")){
            throw new ValidationException(field + " must be between " + MIN_PHONE_LENGTH + " and " + MAX_PHONE_LENGTH + " digits with an optional leading +");
        }
    }

    static void checkNotNull(Object x,String field) throws ValidationException{
        if(x==null){
            throw  new ValidationException(field + " is required");
        }
    }

    static void checkPastDates(LocalDate date, String field) throws ValidationException{
        checkNotNull(date,field);
        if(!date.isBefore(LocalDate.now())){
            throw  new ValidationException(field + " must be in past ");
        }
    }

    static void checkNotFutureDates(LocalDate date,String field) throws ValidationException{
        checkNotNull(date,field);
        if(!date.isAfter(LocalDate.now())){
            throw  new ValidationException(field + " must be in future");
        }
    }

    static LocalDate checkInputDate(String date, String field) throws ValidationException{
        checkNotNull(date, field);
        try{
            return LocalDate.parse(date.trim());
        }
        catch(DateTimeParseException e){
            throw   new ValidationException("invalid date for " + field + " it must look like " + DATE_PATTERN);
        }
    }

    static String formatDate(LocalDate date){
        if(date==null){
            return "-";
        }
        return date.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }
}
