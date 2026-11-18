package com.auca.main;

import com.auca.abstracts.AcademicMember;
import com.auca.abstracts.Person;
import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.bookloan.BookLoan;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.BookUnavailableException;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.prerequisiteMissingException;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.exceptions.ValidationException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static class CancelException extends RuntimeException {}
    private static class BackException extends RuntimeException {}
    private static final Scanner sc = new Scanner(System.in);

    private static final ArrayList<Department> departments = new ArrayList<>();
    private static final ArrayList<Student> students = new ArrayList<>();
    private static final ArrayList<Teacher> teachers = new ArrayList<>();
    private static final ArrayList<Administrator> administrators = new ArrayList<>();
    private static final ArrayList<Course> courses = new ArrayList<>();
    private static final ArrayList<Classroom> classrooms = new ArrayList<>();
    private static final ArrayList<Enrollment> enrollments = new ArrayList<>();
    private static final ArrayList<Assessment> assessments = new ArrayList<>();
    private static final ArrayList<FeePayment> payments = new ArrayList<>();
    private static final ArrayList<LibraryBook> books = new ArrayList<>();
    private static final ArrayList<BookLoan> loans = new ArrayList<>();

     static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMainMenu();
            String input = readRaw(" Select an option: ");
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("! Please type a number from the menu.");
                continue;
            }
            try {
                switch (choice) {
                    case 1 -> register(DEPARTMENT);
                    case 2 -> register(STUDENT);
                    case 3 -> register(TEACHER);
                    case 4 -> register(ADMIN);
                    case 5 -> register(COURSE);
                    case 6 -> register(CLASSROOM);
                    case 7 -> register(ENROLLMENT);
                    case 8 -> register(ASSESSMENT);
                    case 9 -> register(PAYMENT);
                    case 10 -> register(BOOK);
                    case 11 -> register(LOAN);
                    case 12 -> returnBook();
                    case 13 -> viewAllRecords();
                    case 14 -> searchById();
                    case 15 -> updateRecord();
                    case 16 -> showStudentSummary();
                    case 17 -> showPaymentHistory();
                    case 18 -> showAvailableBooks();
                    case 19 -> demonstratePolymorphism();
                    case 0 -> {
                        running = false;
                        System.out.println("Goodbye!");
                    }
                    default -> System.out.println("! Invalid option. Choose a number from 0 to 19.");
                }
            } catch (RecordNotFoundException | ValidationException e) {
                System.out.println("! " + e.getMessage());
            } catch (CancelException | BackException e) {
                System.out.println("! Cancelled. Back to the main menu.");
            } catch (Exception e) {
                System.out.println("! Unexpected problem (" + e + "). Back to the main menu.");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println("=============AUCA_29462 MANAGEMENT SYSTEM ==============");
        System.out.println("1. Register Department.             11. Borrow Library Book.");
        System.out.println("2. Register Student.                12. Return Library Book.");
        System.out.println("3. Register Teacher.                13. View All Records.");
        System.out.println("4. Register Administrator.          14. Search Record by ID.");
        System.out.println("5. Register Course.                 15. Update Existing Record.");
        System.out.println("6. Register Classroom.              16. Student Academic Summary.");
        System.out.println("7. Enroll Student.                  17. Student Payment History.");
        System.out.println("8. Record Assessment.               18. View Available Books.");
        System.out.println("9. Record Fee Payment.              19. Demonstrate Polymorphism.");
        System.out.println("10.Register Library Book.            0. Exit.");
    }

    // FORM: case 1-11
    private static void register(int type) {
        while (true) {
            Identifiable saved = runForm(type);
            if (saved == null) {
                return;
            }
            System.out.println();
            System.out.println(" [1] Register another   [2] View this record   [0] Main menu");
            String c = readRaw("Select option: ");
            if (c.equals("1")) {
                continue;
            }
            if (c.equals("2")) {
                ((Registrable) saved).printDetails();
            }
            return;
        }
    }


    private static Identifiable runForm(int type) {
        int total = NAMES[type].length - 1;
        String[] v = new String[total + 1];
        Object[] r = new Object[total + 1];
        int[] ends = STEP_ENDS[type];
        try {
            int step = 1;
            while (step <= 3) {
                printStepHeader(type, step);
                int from = (step == 1) ? 1 : ends[step - 2] + 1;
                int to = ends[step - 1];
                try {
                    for (int n = from; n <= to; n++) {
                        askField(type, n, v, r);
                    }
                    step++;
                } catch (BackException e) {
                    if (step > 1) {
                        step--;
                    } else {
                        System.out.println("(You are already at the first step.)");
                    }
                }
            }
            return reviewAndSave(type, v, r);
        } catch (CancelException e) {
            System.out.println("\n! Registration cancelled. Nothing was saved.");
            return null;
        } catch (prerequisiteMissingException m) {
            System.out.println("\n! " + m.getMessage());
            System.out.println("  The " + TITLES[type].toLowerCase() + " registration was left uncommitted.");
            return null;
        }
    }

    private static void box(String text) {
        System.out.printf("| %-58s |%n", text);
    }

    private static void printStepHeader(int type, int step) {
        System.out.println();
        System.out.println(LINE);
        box("AUCA_ID / SCHOOL MANAGEMENT");
        box(String.format("%-40s Step %d of 4", TITLES[type] + " REGISTRATION", step));
        System.out.println(LINE);
        box("[STEP " + step + "/4] " + STEP_NAMES[type][step - 1].toUpperCase());
        box("Type BACK for the previous step, CANCEL to discard.");
        box("Press ENTER to keep a value shown as (current: ...).");
        System.out.println(LINE);
    }

    private static void printReview(int type, String[] v) {
        int[] ends = STEP_ENDS[type];
        System.out.println();
        System.out.println(LINE);
        box(String.format("%-40s Step 4 of 4", "REVIEW " + TITLES[type]));
        System.out.println(LINE);
        int section = 0;
        System.out.println("| -- " + STEP_NAMES[type][0].toUpperCase() + " --");
        for (int n = 1; n < NAMES[type].length; n++) {
            if (section < 2 && n == ends[section] + 1) {
                section++;
                System.out.println("| -- " + STEP_NAMES[type][section].toUpperCase() + " --");
            }
            System.out.println(String.format("| %02d %-22s: %s", n, NAMES[type][n], v[n]));
        }
        System.out.println(LINE);
        System.out.println("| [1] Save   [2] Edit field   [0] Cancel");
        System.out.println(LINE);
    }

    private static Identifiable reviewAndSave(int type, String[] v, Object[] r) throws prerequisiteMissingException {
        while (true) {
            printReview(type, v);
            String c = readRaw("Select option: ");
            if (c.equals("1")) {
                try {
                    Identifiable saved = save(type, v, r);
                    System.out.println("\n+ " + TITLES[type] + " " + saved.getId() + " registered successfully.");
                    System.out.println("  " + ((Registrable) saved).getRegistrationSummary());
                    return saved;
                } catch (ValidationException | DuplicateRecordException | BookUnavailableException e) {
                    System.out.println("! Could not save: " + e.getMessage());
                    System.out.println("  Use [2] Edit field to correct it, or [0] to cancel.");
                }
            } else if (c.equals("2")) {
                editField(type, v, r);
            } else if (c.equals("0")) {
                throw new CancelException();
            } else {
                System.out.println("! Choose 1, 2 or 0.");
            }
        }
    }

    private static void editField(int type, String[] v, Object[] r) throws prerequisiteMissingException {
        int total = NAMES[type].length - 1;
        String in = readRaw("Field number (01-" + String.format("%02d", total) + "): ");
        try {
            int n = Integer.parseInt(in);
            if (n < 1 || n > total) {
                System.out.println("! There is no field " + n + ".");
                return;
            }
            System.out.println("Old value: " + v[n]);
            try {
                askField(type, n, v, r);
            } catch (BackException e) {
                System.out.println("(edit abandoned, value unchanged)");
            }
        } catch (NumberFormatException e) {
            System.out.println("! Enter the field number, for example 06.");
        }
    }

    private static String fieldName(int type, int n) {
        return String.format("%02d", n) + " " + NAMES[type][n];
    }

    // Smart selectors (numbered list of real records + nested registration)
    private static Identifiable selectRecord(int kind, boolean activeOnly) throws prerequisiteMissingException {
        String name = TITLES[kind].toLowerCase();
        while (true) {
            ArrayList<Identifiable> options = listFor(kind, activeOnly);
            System.out.println();
            System.out.println(LINE);
            box("CHOOSE " + TITLES[kind]);
            System.out.println(LINE);
            if (options.isEmpty()) {
                System.out.println("No " + (activeOnly ? "active " : "") + name + " is available.");
                System.out.println(" 1 Register " + name + " now");
                System.out.println(" 0 Cancel (the current registration stays unsaved)");
                String c = read("Select option: ");
                if (c.equals("1")) {
                    runForm(kind);
                } else if (c.equals("0")) {
                    throw new prerequisiteMissingException("No " + name
                            + " exists. Register one before continuing.");
                } else {
                    System.out.println("! Choose 1 or 0.");
                }
            } else {
                for (int i = 0; i < options.size(); i++) {
                    System.out.println(" " + (i + 1) + " " + ((Registrable) options.get(i)).getRegistrationSummary());
                }
                int plus = options.size() + 1;
                System.out.println(" " + plus + " [+] Register a new " + name);
                System.out.println(" 0 Cancel");
                String c = read("Select option: ");
                try {
                    int num = Integer.parseInt(c);
                    if (num == 0) {
                        throw new CancelException();
                    }
                    if (num >= 1 && num <= options.size()) {
                        return options.get(num - 1);
                    }
                    if (num == plus) {
                        runForm(kind);
                    } else {
                        System.out.println("! Choose a number between 0 and " + plus + ".");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("! Enter a number from the list.");
                }
            }
        }
    }

    private static void pick(int type, int n, int kind, boolean activeOnly, String[] v, Object[] r)
            throws prerequisiteMissingException {
        if (r[n] != null) {
            String s = read(fieldName(type, n) + " [select] current: " + v[n] + " (ENTER keeps, C changes): ");
            if (!s.equalsIgnoreCase("C")) {
                return;
            }
        }
        Identifiable chosen = selectRecord(kind, activeOnly);
        r[n] = chosen;
        v[n] = ((Registrable) chosen).getRegistrationSummary();
    }

    private static void pickAvailableBook(String[] v, Object[] r) throws prerequisiteMissingException {
        while (true) {
            pick(LOAN, 3, BOOK, false, v, r);
            LibraryBook b = (LibraryBook) r[3];
            try {
                if (b.getAvailableCopies() <= 0) {
                    throw new BookUnavailableException("No copy of \"" + b.getTitle()
                            + "\" is available. Choose another book.");
                }
                return;
            } catch (BookUnavailableException e) {
                System.out.println("! " + e.getMessage());
                r[3] = null;
                v[3] = null;
            }
        }
    }

    // Field questions
    private static void askField(int type, int n, String[] v, Object[] r) throws prerequisiteMissingException {
        switch (type) {
            case DEPARTMENT -> askDepartmentField(n, v);
            case STUDENT, TEACHER, ADMIN -> {
                if (n <= 6) {
                    askPersonField(type, n, v);
                } else if (type == ADMIN) {
                    askAdminField(n, v);
                } else if (n <= 12) {
                    askMemberField(type, n, v, r);
                } else if (type == STUDENT) {
                    askStudentField(n, v);
                } else {
                    askTeacherField(n, v);
                }
            }
            case COURSE -> askCourseField(n, v, r);
            case CLASSROOM -> askClassroomField(n, v);
            case ENROLLMENT -> askEnrollmentField(n, v, r);
            case ASSESSMENT -> askAssessmentField(n, v, r);
            case PAYMENT -> askPaymentField(n, v, r);
            case BOOK -> askBookField(n, v);
            case LOAN -> askLoanField(n, v, r);
        }
    }

    private static void askDepartmentField(int n, String[] v) {
        String name = fieldName(DEPARTMENT, n);
        switch (n) {
            case 1 -> v[1] = askText(name, ID_HINT, v[1], MIN_ID, MAX_ID, "ID");
            case 2 -> v[2] = askText(name, NAME_HINT, v[2], MIN_NAME, MAX_NAME, null);
            case 3 -> v[3] = askText(name, "String | 2-5 chars", v[3], 2, 5, null);
            case 4 -> v[4] = askText(name, "String | 2-50 chars", v[4], 2, 50, null);
            case 5 -> v[5] = askEmail(name, v[5], null);
            case 6 -> v[6] = "none (assign later with menu 15, Update)";
        }
    }

    private static void askPersonField(int type, int n, String[] v) {
        String name = fieldName(type, n);
        switch (n) {
            case 1 -> v[1] = askText(name, ID_HINT, v[1], MIN_ID, MAX_ID, "ID");
            case 2 -> v[2] = askText(name, NAME_HINT, v[2], MIN_NAME, MAX_NAME, null);
            case 3 -> v[3] = askText(name, NAME_HINT, v[3], MIN_NAME, MAX_NAME, null);
            case 4 -> v[4] = askEmail(name, v[4], "EMAIL");
            case 5 -> v[5] = askPhone(name, v[5]);
            case 6 -> v[6] = askDate(name, v[6], PAST, null);
        }
    }

    private static void askMemberField(int type, int n, String[] v, Object[] r) throws prerequisiteMissingException {
        String name = fieldName(type, n);
        switch (n) {
            case 7 -> v[7] = askText(name, ID_HINT, v[7], MIN_ID, MAX_ID, "MEMBER");
            case 8 -> pick(type, 8, DEPARTMENT, false, v, r);
            case 9 -> v[9] = askDate(name, v[9], NOT_FUTURE, null);
            case 10 -> v[10] = askChoice(name, v[10], AcademicMember.allowedStatuses());
            case 11 -> v[11] = askEmail(name, v[11], "INSTEMAIL");
            case 12 -> v[12] = askText(name, "String | " + MIN_ID + "-" + MAX_ID + " chars", v[12], MIN_ID, MAX_ID, null);
        }
    }

    private static void askStudentField(int n, String[] v) {
        String name = fieldName(STUDENT, n);
        switch (n) {
            case 13 -> v[13] = askText(name, ID_HINT, v[13], MIN_ID, MAX_ID, "STUDENTNO");
            case 14 -> v[14] = askText(name, NAME_HINT, v[14], MIN_NAME, MAX_NAME, null);
            case 15 -> v[15] = askDate(name, v[15], NOT_FUTURE, null);
            case 16 -> v[16] = askInt(name, v[16], 1, Student.maxStudyYear());
            case 17 -> v[17] = askPhone(name, v[17]);
            case 18 -> v[18] = askChoice(name, v[18], YES_NO);
        }
    }

    private static void askTeacherField(int n, String[] v) {
        String name = fieldName(TEACHER, n);
        switch (n) {
            case 13 -> v[13] = askText(name, "String | unique | " + MIN_ID + "-" + Registrable.MAX_PHONE_LENGTH + " chars",
                    v[13], MIN_ID, Registrable.MAX_PHONE_LENGTH, "EMPNO");
            case 14 -> v[14] = askText(name, "String | " + MIN_NAME + "-100 chars", v[14], MIN_NAME, 100, null);
            case 15 -> v[15] = askDate(name, v[15], NOT_FUTURE, null);
            case 16 -> v[16] = askAmount(name, v[16]);
            case 17 -> v[17] = askText(name, "String | 1-3 chars", v[17], 1, 3, null);
            case 18 -> v[18] = askChoice(name, v[18], Teacher.allowedEmployeeTypes());
        }
    }

    private static void askAdminField(int n, String[] v) {
        String name = fieldName(ADMIN, n);
        switch (n) {
            case 7 -> v[7] = askText(name, ID_HINT, v[7], MIN_ID, MAX_ID, "STAFFNO");
            case 8 -> v[8] = askText(name, "String | " + MIN_NAME + "-100 chars", v[8], MIN_NAME, 100, null);
            case 9 -> v[9] = askText(name, "String | 1-60 chars", v[9], 1, 60, null);
            case 10 -> v[10] = askDate(name, v[10], NOT_FUTURE, null);
            case 11 -> v[11] = askInt(name, v[11], 1, Administrator.maxAccessLevel());
            case 12 -> v[12] = askChoice(name, v[12], YES_NO);
        }
    }

    private static void askCourseField(int n, String[] v, Object[] r) throws prerequisiteMissingException {
        String name = fieldName(COURSE, n);
        switch (n) {
            case 1 -> v[1] = askText(name, ID_HINT, v[1], MIN_ID, MAX_ID, "ID");
            case 2 -> v[2] = askText(name, "String | unique | 2-10 chars", v[2], 2, 10, "COURSECODE");
            case 3 -> v[3] = askText(name, "String | " + MIN_ID + "-" + MAX_ID + " chars", v[3], MIN_ID, MAX_ID, null);
            case 4 -> v[4] = askInt(name, v[4], 1, Course.maxCredit());
            case 5 -> pick(COURSE, 5, DEPARTMENT, false, v, r);
            case 6 -> pick(COURSE, 6, TEACHER, false, v, r);
        }
    }

    private static void askClassroomField(int n, String[] v) {
        String name = fieldName(CLASSROOM, n);
        switch (n) {
            case 1 -> v[1] = askText(name, ID_HINT, v[1], MIN_ID, MAX_ID, "ID");
            case 2 -> v[2] = askText(name, NAME_HINT, v[2], MIN_NAME, MAX_NAME, null);
            case 3 -> v[3] = askText(name, "String | 1-10 chars", v[3], 1, 10, null);
            case 4 -> v[4] = askInt(name, v[4], 1, 10000);
            case 5 -> v[5] = askInt(name, v[5], 0, 100);
            case 6 -> v[6] = askChoice(name, v[6], YES_NO);
        }
    }

    private static void askEnrollmentField(int n, String[] v, Object[] r) throws prerequisiteMissingException {
        String name = fieldName(ENROLLMENT, n);
        switch (n) {
            case 1 -> v[1] = askText(name, ID_HINT, v[1], MIN_ID, MAX_ID, "ID");
            case 2 -> pick(ENROLLMENT, 2, STUDENT, false, v, r);
            case 3 -> pick(ENROLLMENT, 3, COURSE, false, v, r);
            case 4 -> v[4] = askDate(name, v[4], NOT_FUTURE, null);
            case 5 -> v[5] = askChoice(name, v[5], Enrollment.allowedSemester());
            case 6 -> v[6] = askChoice(name, v[6], Enrollment.allowedStatuses());
        }
    }

    private static void askAssessmentField(int n, String[] v, Object[] r) throws prerequisiteMissingException {
        String name = fieldName(ASSESSMENT, n);
        switch (n) {
            case 1 -> v[1] = askText(name, ID_HINT, v[1], MIN_ID, MAX_ID, "ID");
            case 2 -> pick(ASSESSMENT, 2, ENROLLMENT, true, v, r);
            case 3 -> v[3] = askChoice(name, v[3], Assessment.allowedAssessmentTypes());
            case 4 -> v[4] = askDouble(name, v[4], "double | greater than 0", 0, Double.MAX_VALUE, true);
            case 5 -> {
                double max = Double.parseDouble(v[4]);
                v[5] = askDouble(name, v[5], "double | 0 to " + v[4], 0, max, false);
            }
            case 6 -> v[6] = askDate(name, v[6], NOT_FUTURE, null);
        }
    }

    private static void askPaymentField(int n, String[] v, Object[] r) throws prerequisiteMissingException {
        String name = fieldName(PAYMENT, n);
        switch (n) {
            case 1 -> v[1] = askText(name, ID_HINT, v[1], MIN_ID, MAX_ID, "ID");
            case 2 -> pick(PAYMENT, 2, STUDENT, false, v, r);
            case 3 -> v[3] = askAmount(name, v[3]);
            case 4 -> v[4] = askDate(name, v[4], NOT_FUTURE, null);
            case 5 -> v[5] = askChoice(name, v[5], FeePayment.allowedMethods());
            case 6 -> v[6] = askText(name, "String | unique | 1-30 chars", v[6], 1, 30, "RECEIPT");
        }
    }

    private static void askBookField(int n, String[] v) {
        String name = fieldName(BOOK, n);
        switch (n) {
            case 1 -> v[1] = askText(name, ID_HINT, v[1], MIN_ID, MAX_ID, "ID");
            case 2 -> v[2] = askText(name, "String | unique | 1-30 chars", v[2], 1, 30, "ISBN");
            case 3 -> v[3] = askText(name, "String | 1-100 chars", v[3], 1, 100, null);
            case 4 -> v[4] = askText(name, NAME_HINT, v[4], MIN_NAME, MAX_NAME, null);
            case 5 -> v[5] = askInt(name, v[5], 0, 10000);
            case 6 -> {
                int total = Integer.parseInt(v[5]);
                v[6] = askInt(name, v[6], 0, total);
            }
        }
    }

    private static void askLoanField(int n, String[] v, Object[] r) throws prerequisiteMissingException {
        String name = fieldName(LOAN, n);
        switch (n) {
            case 1 -> v[1] = askText(name, ID_HINT, v[1], MIN_ID, MAX_ID, "ID");
            case 2 -> pick(LOAN, 2, STUDENT, false, v, r);
            case 3 -> pickAvailableBook(v, r);
            case 4 -> v[4] = askDate(name, v[4], NOT_FUTURE, null);
            case 5 -> v[5] = askDate(name, v[5], ANY, LocalDate.parse(v[4]));
            case 6 -> v[6] = "(not returned yet)";
        }
    }

    // Saving records
    private static Identifiable save(int type, String[] v, Object[] r)
            throws ValidationException, DuplicateRecordException, BookUnavailableException {
        return switch (type) {
            case DEPARTMENT -> saveDepartment(v);
            case STUDENT -> saveStudent(v, r);
            case TEACHER -> saveTeacher(v, r);
            case ADMIN -> saveAdministrator(v);
            case COURSE -> saveCourse(v, r);
            case CLASSROOM -> saveClassroom(v);
            case ENROLLMENT -> saveEnrollment(v, r);
            case ASSESSMENT -> saveAssessment(v, r);
            case PAYMENT -> savePayment(v, r);
            case BOOK -> saveBook(v);
            default -> saveLoan(v, r);
        };
    }

    private static Identifiable saveDepartment(String[] v) throws ValidationException, DuplicateRecordException {
        requireUnique("ID", v[1], "Department ID");
        Department d = new Department(v[1], v[2], v[3], v[4], v[5]);
        d.validate();
        departments.add(d);
        return d;
    }

    private static Identifiable saveStudent(String[] v, Object[] r) throws ValidationException, DuplicateRecordException {
        requireUnique("ID", v[1], "Person ID");
        requireUnique("EMAIL", v[4], "Email");
        requireUnique("MEMBER", v[7], "Member number");
        requireUnique("Institutional EMAIL", v[11], "Institutional email");
        requireUnique("STUDENT NO", v[13], "Student number");
        Student s = new Student(v[1], v[2], v[3], v[4], v[5], LocalDate.parse(v[6]),
                v[7], (Department) r[8], LocalDate.parse(v[9]), v[10], v[11], v[12],
                v[13], v[14], LocalDate.parse(v[15]), Integer.parseInt(v[16]), v[17], v[18].equals("YES"));
        s.validate();
        students.add(s);
        return s;
    }

    private static Identifiable saveTeacher(String[] v, Object[] r) throws ValidationException, DuplicateRecordException {
        requireUnique("ID", v[1], "Person ID");
        requireUnique("EMAIL", v[4], "Email");
        requireUnique("MEMBER", v[7], "Member number");
        requireUnique("Institutional EMAIL", v[11], "Institutional email");
        requireUnique("EMPNO", v[13], "Employee number");
        Teacher t = new Teacher(v[1], v[2], v[3], v[4], v[5], LocalDate.parse(v[6]),
                v[7], (Department) r[8], LocalDate.parse(v[9]), v[10], v[11], v[12],
                v[13], v[14], LocalDate.parse(v[15]), new BigDecimal(v[16]), v[17], v[18]);
        t.validate();
        teachers.add(t);
        return t;
    }

    private static Identifiable saveAdministrator(String[] v) throws ValidationException, DuplicateRecordException {
        requireUnique("ID", v[1], "Person ID");
        requireUnique("EMAIL", v[4], "Email");
        requireUnique("STAFF NO", v[7], "Staff number");
        Administrator a = new Administrator(v[1], v[2], v[3], v[4], v[5], LocalDate.parse(v[6]),
                v[7], v[8], v[9], LocalDate.parse(v[10]), Integer.parseInt(v[11]), v[12].equals("YES"));
        a.validate();
        administrators.add(a);
        return a;
    }

    private static Identifiable saveCourse(String[] v, Object[] r) throws ValidationException, DuplicateRecordException {
        requireUnique("ID", v[1], "Course ID");
        requireUnique("COURSECODE", v[2], "Course code");
        Course c = new Course(v[1], v[2], v[3], Integer.parseInt(v[4]), (Department) r[5], (Teacher) r[6]);
        c.validate();
        courses.add(c);
        return c;
    }

    private static Identifiable saveClassroom(String[] v) throws ValidationException, DuplicateRecordException {
        requireUnique("ID", v[1], "Classroom ID");
        Classroom c = new Classroom(v[1], v[2], v[3], Integer.parseInt(v[4]), Integer.parseInt(v[5]), v[6].equals("YES"));
        c.validate();
        classrooms.add(c);
        return c;
    }

    private static Identifiable saveEnrollment(String[] v, Object[] r) throws ValidationException, DuplicateRecordException {
        requireUnique("ID", v[1], "Enrollment ID");
        Student s = (Student) r[2];
        Course c = (Course) r[3];
        if (v[6].equalsIgnoreCase(Identifiable.DEFAULT_STATUS) && activeEnrollmentExists(s, c, v[5], null)) {
            throw new DuplicateRecordException(s.getDisplayName() + " already has an active enrollment in "
                    + c.getCourseCode() + " for the " + v[5] + " semester.");
        }
        Enrollment e = new Enrollment(v[1], s, c, LocalDate.parse(v[4]), v[5], v[6]);
        e.validate();
        enrollments.add(e);
        return e;
    }

    private static Identifiable saveAssessment(String[] v, Object[] r) throws ValidationException, DuplicateRecordException {
        requireUnique("ID", v[1], "Assessment ID");
        Enrollment en = (Enrollment) r[2];
        if (!en.isActive()) {
            throw new ValidationException("Only an active enrollment can be assessed.");
        }
        Assessment a = new Assessment(v[1], en, v[3], Double.parseDouble(v[4]), Double.parseDouble(v[5]),
                LocalDate.parse(v[6]));
        a.validate();
        assessments.add(a);
        return a;
    }

    private static Identifiable savePayment(String[] v, Object[] r) throws ValidationException, DuplicateRecordException {
        requireUnique("ID", v[1], "Payment ID");
        requireUnique("RECEIPT", v[6], "Receipt number");
        FeePayment p = new FeePayment(v[1], (Student) r[2], new BigDecimal(v[3]), LocalDate.parse(v[4]), v[5], v[6]);
        p.validate();
        payments.add(p);
        return p;
    }

    private static Identifiable saveBook(String[] v) throws ValidationException, DuplicateRecordException {
        requireUnique("ID", v[1], "Book ID");
        requireUnique("ISBN", v[2], "ISBN");
        LibraryBook b = new LibraryBook(v[1], v[2], v[3], v[4], Integer.parseInt(v[5]), Integer.parseInt(v[6]));
        b.validate();
        books.add(b);
        return b;
    }

    private static Identifiable saveLoan(String[] v, Object[] r)
            throws ValidationException, DuplicateRecordException, BookUnavailableException {
        requireUnique("ID", v[1], "Loan ID");
        LibraryBook book = (LibraryBook) r[3];
        if (book.getAvailableCopies() <= 0) {
            throw new BookUnavailableException("No copy of \"" + book.getTitle() + "\" is available.");
        }
        BookLoan loan = new BookLoan(v[1], (Student) r[2], book, LocalDate.parse(v[4]), LocalDate.parse(v[5]));
        loan.validate();
        loans.add(loan);
        book.setAvailableCopies(book.getAvailableCopies() - 1);   // stock goes down exactly once
        System.out.println("  Stock updated: " + book.getAvailableCopies() + " of " + book.getTotalCopies()
                + " copies now available.");
        return loan;
    }


//case 12: returning book
    private static void returnBook() throws ValidationException {
        ArrayList<Identifiable> active = new ArrayList<>();
        for (BookLoan l : loans) {
            if (l.isActive()) {
                active.add(l);
            }
        }
        System.out.println("\n----------- RETURN LIBRARY BOOK -----------");
        Identifiable chosen = chooseFrom(active, "No book is currently on loan. Borrow a book first (menu 11).");
        if (chosen == null) {
            return;
        }
        BookLoan loan = (BookLoan) chosen;
        LocalDate returnDate = null;
        while (returnDate == null) {
            String in = read("Return date [LocalDate | YYYY-MM-DD] (CANCEL to stop): ");
            try {
                LocalDate d = LocalDate.parse(in);
                if (d.isAfter(LocalDate.now())) {
                    throw new ValidationException("Return date must not be in the future");
                }
                if (d.isBefore(loan.getBorrowedDate())) {
                    throw new ValidationException("Returned date must not be before the borrowed date ("
                            + loan.getBorrowedDate() + ")");
                }
                returnDate = d;
            } catch (DateTimeParseException e) {
                System.out.println("! Invalid date: use YYYY-MM-DD (example: 2026-11-18).");
            } catch (ValidationException e) {
                System.out.println("! " + e.getMessage());
            }
        }
        LibraryBook book = loan.getBook();
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new ValidationException("Stock error: all copies of this book are already in the library.");
        }
        if (!confirm("Confirm return of \"" + book.getTitle() + "\" by " + loan.getStudent().getDisplayName() + "?")) {
            System.out.println("Return cancelled. Nothing was changed.");
            return;
        }
        loan.setReturnedDate(returnDate);
        book.setAvailableCopies(book.getAvailableCopies() + 1);     // stock goes up exactly once
        System.out.println("+ Book returned. " + book.getAvailableCopies() + " of " + book.getTotalCopies()
                + " copies are available.");
        long lateDays = returnDate.toEpochDay() - loan.getDueDate().toEpochDay();
        if (lateDays > 0) {
            System.out.println("  Note: returned " + lateDays + " day(s) after the due date.");
        }
    }


    // case 13: view all records
    private static void viewAllRecords() {
        for (int kind = DEPARTMENT; kind <= LOAN; kind++) {
            ArrayList<Identifiable> list = listFor(kind, false);
            System.out.println("\n######## " + TITLES[kind] + " (" + list.size() + ") ########");
            if (list.isEmpty()) {
                System.out.println("  (no records)");
            }
            for (Identifiable item : list) {
                System.out.println("  " + ((Registrable) item).getRegistrationSummary());
            }
        }
        System.out.println();
        pause();
    }


//case 14: searching using IDs
    private static void searchById() throws RecordNotFoundException {
        String id = readRaw("Enter the record ID to search: ");
        if (id.isEmpty()) {
            System.out.println("! Nothing typed.");
            return;
        }
        for (Identifiable item : allRecords()) {
            if (item.getId().equalsIgnoreCase(id)) {
                System.out.println("Record found:");
                ((Registrable) item).printDetails();
                return;
            }
        }
        throw new RecordNotFoundException("No record found with ID \"" + id + "\".");
    }

    private static Identifiable chooseFrom(ArrayList<Identifiable> list, String emptyMessage) {
        if (list.isEmpty()) {
            System.out.println(emptyMessage);
            return null;
        }
        while (true) {
            System.out.println();
            for (int i = 0; i < list.size(); i++) {
                System.out.println(" " + (i + 1) + " " + ((Registrable) list.get(i)).getRegistrationSummary());
            }
            System.out.println(" 0 Cancel");
            String c = readRaw("Select: ");
            try {
                int num = Integer.parseInt(c);
                if (num == 0) {
                    return null;
                }
                if (num >= 1 && num <= list.size()) {
                    return list.get(num - 1);
                }
                System.out.println("! Choose a number between 0 and " + list.size() + ".");
            } catch (NumberFormatException e) {
                System.out.println("! Enter a number from the list.");
            }
        }
    }

    private static Identifiable chooseExisting(int kind) {
        return chooseFrom(listFor(kind, false), "No " + TITLES[kind].toLowerCase() + " has been registered yet.");
    }

    // case 15:updating records
    private static void updateRecord() {
        System.out.println("\n----------- UPDATE EXISTING RECORD -----------");
        System.out.println(" 1 Department   2 Student");
        System.out.println(" 3 Teacher      4 Administrator");
        System.out.println(" 5 Course       6 Classroom");
        System.out.println(" 7 Enrollment   8 Assessment");
        System.out.println(" 9 Library book   0 Cancel");
        System.out.println(" (Fee payments and book loans are history and are not edited; use menu 12 to return a book.)");
        String c = readRaw("Select the kind of record: ");
        int kind = switch (c) {
            case "1" -> DEPARTMENT;
            case "2" -> STUDENT;
            case "3" -> TEACHER;
            case "4" -> ADMIN;
            case "5" -> COURSE;
            case "6" -> CLASSROOM;
            case "7" -> ENROLLMENT;
            case "8" -> ASSESSMENT;
            case "9" -> BOOK;
            case "0" -> 0;
            default -> -1;
        };
        if (kind == 0) {
            return;
        }
        if (kind == -1) {
            System.out.println("! Invalid choice.");
            return;
        }
        Identifiable rec = chooseExisting(kind);
        if (rec == null) {
            return;
        }
        System.out.println("\nCurrent record:");
        ((Registrable) rec).printDetails();
        System.out.println("\n(IDs cannot be changed.)");
        switch (kind) {
            case DEPARTMENT -> updateDepartment((Department) rec);
            case STUDENT -> updateStudent((Student) rec);
            case TEACHER -> updateTeacher((Teacher) rec);
            case ADMIN -> updateAdministrator((Administrator) rec);
            case COURSE -> updateCourse((Course) rec);
            case CLASSROOM -> updateClassroom((Classroom) rec);
            case ENROLLMENT -> updateEnrollment((Enrollment) rec);
            case ASSESSMENT -> updateAssessment((Assessment) rec);
            case BOOK -> updateBook((LibraryBook) rec);
        }
    }

    private static int pickField(String menu) {
        System.out.println(menu);
        String c = readRaw("Field to change (0 = cancel): ");
        try {
            return Integer.parseInt(c);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static boolean confirmChange(String what, String value) {
        boolean ok = confirm("Change " + what + " to \"" + value + "\"?");
        System.out.println(ok ? "+ Updated successfully." : "No change made.");
        return ok;
    }

    private static boolean updatePersonField(Person p, int c) {
        switch (c) {
            case 1 -> {
                String x = askText("First name", NAME_HINT, p.getFirstName(), MIN_NAME, MAX_NAME, null);
                if (confirmChange("first name", x)) p.setFirstName(x);
            }
            case 2 -> {
                String x = askText("Last name", NAME_HINT, p.getLastName(), MIN_NAME, MAX_NAME, null);
                if (confirmChange("last name", x)) p.setLastName(x);
            }
            case 3 -> {
                String x = askEmail("Email", p.getEmail(), "EMAIL");
                if (confirmChange("email", x)) p.setEmail(x);
            }
            case 4 -> {
                String x = askPhone("Phone", p.getPhoneNumber());
                if (confirmChange("phone", x)) p.setPhoneNumber(x);
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    private static void updateDepartment(Department d) {
        int c = pickField(" 1 Name   2 Code   3 Office location   4 Contact email   5 Head teacher");
        switch (c) {
            case 0 -> System.out.println("No change made.");
            case 1 -> {
                String x = askText("Department name", NAME_HINT, d.getDepartmentName(), MIN_NAME, MAX_NAME, null);
                if (confirmChange("name", x)) d.setDepartmentName(x);
            }
            case 2 -> {
                String x = askText("Department code", "String | 2-5 chars", d.getDepartmentCode(), 2, 5, null);
                if (confirmChange("code", x)) d.setDepartmentCode(x);
            }
            case 3 -> {
                String x = askText("Office location", "String | 2-50 chars", d.getOfficeLocation(), 2, 50, null);
                if (confirmChange("office location", x)) d.setOfficeLocation(x);
            }
            case 4 -> {
                String x = askEmail("Contact email", d.getContactEmail(), null);
                if (confirmChange("contact email", x)) d.setContactEmail(x);
            }
            case 5 -> {
                ArrayList<Identifiable> candidates = new ArrayList<>();
                for (Teacher t : teachers) {
                    if (t.getDepartment() == d) {
                        candidates.add(t);
                    }
                }
                System.out.println("Teachers of this department:");
                Identifiable chosen = chooseFrom(candidates,
                        "No teacher belongs to this department yet. Register a teacher in it first (menu 3).");
                if (chosen != null && confirmChange("head teacher", chosen.getDisplayName())) {
                    d.setHeadTeacher((Teacher) chosen);
                }
            }
            default -> System.out.println("! Invalid field.");
        }
    }

    private static void updateStudent(Student s) {
        int c = pickField(" 1 First name   2 Last name   3 Email   4 Phone\n"
                + " 5 Academic status   6 Program name   7 Study year   8 Guardian phone   9 Scholarship");
        if (c == 0) {
            System.out.println("No change made.");
            return;
        }
        if (updatePersonField(s, c)) {
            return;
        }
        switch (c) {
            case 5 -> {
                String x = askChoice("Academic status", s.getAcademicStatus(), AcademicMember.allowedStatuses());
                if (confirmChange("academic status", x)) s.setAcademicStatus(x);
            }
            case 6 -> {
                String x = askText("Program name", NAME_HINT, s.getProgramName(), MIN_NAME, MAX_NAME, null);
                if (confirmChange("program name", x)) s.setProgramName(x);
            }
            case 7 -> {
                String x = askInt("Study year", String.valueOf(s.getStudyYear()), 1, Student.maxStudyYear());
                if (confirmChange("study year", x)) s.setStudyYear(Integer.parseInt(x));
            }
            case 8 -> {
                String x = askPhone("Guardian phone", s.getGuardianPhone());
                if (confirmChange("guardian phone", x)) s.setGuardianPhone(x);
            }
            case 9 -> {
                String x = askChoice("Scholarship", s.isScholarshipStatus() ? "YES" : "NO", YES_NO);
                if (confirmChange("scholarship", x)) s.setScholarshipStatus(x.equals("YES"));
            }
            default -> System.out.println("! Invalid field.");
        }
    }

    private static void updateTeacher(Teacher t) {
        int c = pickField(" 1 First name   2 Last name   3 Email   4 Phone\n"
                + " 5 Academic status   6 Specialization   7 Salary   8 Office number   9 Employment type");
        if (c == 0) {
            System.out.println("No change made.");
            return;
        }
        if (updatePersonField(t, c)) {
            return;
        }
        switch (c) {
            case 5 -> {
                String x = askChoice("Academic status", t.getAcademicStatus(), AcademicMember.allowedStatuses());
                if (confirmChange("academic status", x)) t.setAcademicStatus(x);
            }
            case 6 -> {
                String x = askText("Specialization", "String | 2-100 chars", t.getSpecialization(), MIN_NAME, 100, null);
                if (confirmChange("specialization", x)) t.setSpecialization(x);
            }
            case 7 -> {
                String x = askAmount("Salary", t.getSalary().toPlainString());
                if (confirmChange("salary", x)) t.setSalary(new BigDecimal(x));
            }
            case 8 -> {
                String x = askText("Office number", "String | 1-3 chars", t.getOfficeNumber(), 1, 3, null);
                if (confirmChange("office number", x)) t.setOfficeNumber(x);
            }
            case 9 -> {
                String x = askChoice("Employment type", t.getEmploymentType(), Teacher.allowedEmployeeTypes());
                if (confirmChange("employment type", x)) t.setEmploymentType(x);
            }
            default -> System.out.println("! Invalid field.");
        }
    }

    private static void updateAdministrator(Administrator a) {
        int c = pickField(" 1 First name   2 Last name   3 Email   4 Phone\n"
                + " 5 Job title   6 Assigned office   7 Access level   8 Active");
        if (c == 0) {
            System.out.println("No change made.");
            return;
        }
        if (updatePersonField(a, c)) {
            return;
        }
        switch (c) {
            case 5 -> {
                String x = askText("Job title", "String | 2-100 chars", a.getJobTitle(), MIN_NAME, 100, null);
                if (confirmChange("job title", x)) a.setJobTitle(x);
            }
            case 6 -> {
                String x = askText("Assigned office", "String | 1-60 chars", a.getAssignedOffice(), 1, 60, null);
                if (confirmChange("assigned office", x)) a.setAssignedOffice(x);
            }
            case 7 -> {
                String x = askInt("Access level", String.valueOf(a.getAccessLevel()), 1, Administrator.maxAccessLevel());
                if (confirmChange("access level", x)) a.setAccessLevel(Integer.parseInt(x));
            }
            case 8 -> {
                String x = askChoice("Active", a.isActive() ? "YES" : "NO", YES_NO);
                if (confirmChange("active", x)) a.setActive(x.equals("YES"));
            }
            default -> System.out.println("! Invalid field.");
        }
    }

    private static void updateCourse(Course course) {
        int c = pickField(" 1 Course title   2 Credit hours   3 Assigned teacher");
        switch (c) {
            case 0 -> System.out.println("No change made.");
            case 1 -> {
                String x = askText("Course title", "String | " + MIN_ID + "-" + MAX_ID + " chars",
                        course.getCourseTitle(), MIN_ID, MAX_ID, null);
                if (confirmChange("course title", x)) course.setCourseTitle(x);
            }
            case 2 -> {
                String x = askInt("Credit hours", String.valueOf(course.getCreditHours()), 1, Course.maxCredit());
                if (confirmChange("credit hours", x)) course.setCreditHours(Integer.parseInt(x));
            }
            case 3 -> {
                Identifiable chosen = chooseExisting(TEACHER);
                if (chosen != null && confirmChange("assigned teacher", chosen.getDisplayName())) {
                    course.setAssignedteacher((Teacher) chosen);
                }
            }
            default -> System.out.println("! Invalid field.");
        }
    }

    private static void updateClassroom(Classroom room) {
        int c = pickField(" 1 Building name   2 Room number   3 Capacity   4 Floor number   5 Available");
        switch (c) {
            case 0 -> System.out.println("No change made.");
            case 1 -> {
                String x = askText("Building name", NAME_HINT, room.getBuildingName(), MIN_NAME, MAX_NAME, null);
                if (confirmChange("building name", x)) room.setBuildingName(x);
            }
            case 2 -> {
                String x = askText("Room number", "String | 1-10 chars", room.getRoomNumber(), 1, 10, null);
                if (confirmChange("room number", x)) room.setRoomNumber(x);
            }
            case 3 -> {
                String x = askInt("Capacity", String.valueOf(room.getCapacity()), 1, 10000);
                if (confirmChange("capacity", x)) room.setCapacity(Integer.parseInt(x));
            }
            case 4 -> {
                String x = askInt("Floor number", String.valueOf(room.getFloorNumber()), 0, 100);
                if (confirmChange("floor number", x)) room.setFloorNumber(Integer.parseInt(x));
            }
            case 5 -> {
                String x = askChoice("Available", room.isAvailable() ? "YES" : "NO", YES_NO);
                if (confirmChange("available", x)) room.setAvailable(x.equals("YES"));
            }
            default -> System.out.println("! Invalid field.");
        }
    }

    private static void updateEnrollment(Enrollment e) {
        int c = pickField(" 1 Enrollment status");
        if (c == 0) {
            System.out.println("No change made.");
        } else if (c == 1) {
            String x = askChoice("Enrollment status", e.getEnrollmentStatus(), Enrollment.allowedStatuses());
            if (x.equalsIgnoreCase(Identifiable.DEFAULT_STATUS)
                    && activeEnrollmentExists(e.getStudent(), e.getCourse(), e.getSemester(), e)) {
                System.out.println("! Another active enrollment already exists for this student, course and semester.");
                return;
            }
            if (confirmChange("enrollment status", x)) e.setEnrollmentStatus(x);
        } else {
            System.out.println("! Invalid field.");
        }
    }

    private static void updateAssessment(Assessment a) {
        int c = pickField(" 1 Obtained score");
        if (c == 0) {
            System.out.println("No change made.");
        } else if (c == 1) {
            String x = askDouble("Obtained score", String.valueOf(a.getObtainedScore()),
                    "double | 0 to " + a.getMaximumScore(), 0, a.getMaximumScore(), false);
            if (confirmChange("obtained score", x)) a.setObtainedScore(Double.parseDouble(x));
        } else {
            System.out.println("! Invalid field.");
        }
    }

    private static void updateBook(LibraryBook b) {
        int c = pickField(" 1 Title   2 Author   3 Total copies");
        switch (c) {
            case 0 -> System.out.println("No change made.");
            case 1 -> {
                String x = askText("Title", "String | 1-100 chars", b.getTitle(), 1, 100, null);
                if (confirmChange("title", x)) b.setTitle(x);
            }
            case 2 -> {
                String x = askText("Author", NAME_HINT, b.getAuthor(), MIN_NAME, MAX_NAME, null);
                if (confirmChange("author", x)) b.setAuthor(x);
            }
            case 3 -> {
                String x = askInt("Total copies", String.valueOf(b.getTotalCopies()), b.getAvailableCopies(), 10000);
                if (confirmChange("total copies", x)) b.setTotalCopies(Integer.parseInt(x));
            }
            default -> System.out.println("! Invalid field.");
        }
    }

//case 16: showing student academic summary
    private static void showStudentSummary() {
        System.out.println("\n----------- STUDENT ACADEMIC SUMMARY -----------");
        Student s = (Student) chooseExisting(STUDENT);
        if (s == null) {
            return;
        }
        System.out.println("\nStudent    : " + s.getRegistrationSummary());
        System.out.println("Department : " + s.getDepartment().getDisplayName());
        System.out.println("Study year : " + s.getStudyYear() + "   Status: " + s.getAcademicStatus());
        double totalPercent = 0;
        int count = 0;
        int enrolled = 0;
        System.out.println("Enrollments:");
        for (Enrollment e : enrollments) {
            if (e.getStudent() != s) {
                continue;
            }
            enrolled++;
            System.out.println(" - " + e.getCourse().getCourseCode() + " " + e.getCourse().getCourseTitle()
                    + " (" + e.getSemester() + ", " + e.getEnrollmentStatus() + ", "
                    + e.getCourse().getCreditHours() + " credits)");
            for (Assessment a : assessments) {
                if (a.getEnrollment() != e) {
                    continue;
                }
                System.out.println("     " + a.getAssessmentType() + ": " + a.getObtainedScore() + "/"
                        + a.getMaximumScore() + String.format(" (%.1f%%)", a.getPercentage()));
                totalPercent += a.getPercentage();
                count++;
            }
        }
        if (enrolled == 0) {
            System.out.println(" (this student has no enrollments yet)");
        }
        if (count > 0) {
            System.out.println(String.format("Average assessment score: %.1f%% over %d assessment(s)",
                    totalPercent / count, count));
        }
        System.out.println();
        pause();
    }

//case 17: show payment history
    private static void showPaymentHistory() {
        System.out.println("\n----------- STUDENT PAYMENT HISTORY -----------");
        Student s = (Student) chooseExisting(STUDENT);
        if (s == null) {
            return;
        }
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        System.out.println("\nPayments of " + s.getDisplayName() + ":");
        for (FeePayment p : payments) {
            if (p.getStudent() == s) {
                System.out.println(" - " + p.getRegistrationSummary());
                total = total.add(p.getAmount());
                count++;
            }
        }
        if (count == 0) {
            System.out.println(" (no payments recorded for this student)");
        } else {
            System.out.println("Total paid: " + total + " in " + count + " payment(s)");
        }
        System.out.println();
        pause();
    }
// case 18: show available books
    private static void showAvailableBooks() {
        System.out.println("\n----------- AVAILABLE BOOKS -----------");
        int count = 0;
        for (LibraryBook b : books) {
            if (b.getAvailableCopies() > 0) {
                System.out.println(" - " + b.getRegistrationSummary());
                count++;
            }
        }
        if (count == 0) {
            System.out.println(" No book has an available copy right now.");
        }
        System.out.println();
        pause();
    }

    //  case 19: POLYMORPHISM DEMONSTRATION
    private static void demonstratePolymorphism() {
        System.out.println("\n===== 1. List<Person>: one call, different behaviour =====");
        List<Person> people = allPeople();
        if (people.isEmpty()) {
            System.out.println("(No student, teacher or administrator registered yet.)");
        }
        for (Person p : people) {
            System.out.println("\n[" + p.getRoleDescription() + "] summary  : " + p.getRegistrationSummary());
            System.out.println("[" + p.getRoleDescription() + "] active   : " + p.isActive());
            if (p instanceof AcademicMember) {
                System.out.println("[" + p.getRoleDescription() + "] category : "
                        + ((AcademicMember) p).getAcademicCategory());
            }
            p.printDetails();
        }

        System.out.println("\n===== 2. List<Identifiable>: unrelated classes, same interface =====");
        List<Identifiable> items = allRecords();
        if (items.isEmpty()) {
            System.out.println("(No records registered yet.)");
        }
        for (Identifiable item : items) {
            System.out.println(item.getId() + " | " + item.getDisplayName() + " | active: " + item.isActive());
        }

        System.out.println("\n===== 3. Explanation =====");
        System.out.println("Multilevel inheritance   : Person -> AcademicMember -> Student / Teacher");
        System.out.println("Hierarchical inheritance : Person -> AcademicMember and Person -> Administrator");
        System.out.println("Constructor chaining     : Student(...) calls super(...) -> AcademicMember -> Person");
        System.out.println("Person and AcademicMember are abstract: they declare abstract methods");
        System.out.println("(getRoleDescription, getAcademicCategory) with no body, so 'new Person(...)'");
        System.out.println("will not compile. Only Student, Teacher, Administrator can be created.");
        System.out.println();
        pause();
    }

    // UPDATE EXISTING RECORD


    //----------------------helper methods------------------------------------------------
    //1.Reads one full line (no BACK/CANCEL handling).
    private static String readRaw(String prompt) {
        System.out.print(prompt);
        if (!sc.hasNextLine()) {
            System.out.println("\nInput closed. Goodbye.");
            System.exit(0);
        }
        return sc.nextLine().trim();
    }

    //2.Reads one line inside a form: CANCEL and BACK leave the current step.
    private static String read(String prompt) {
        String s = readRaw(prompt);
        if (s.equalsIgnoreCase("CANCEL")) {
            throw new CancelException();
        }
        if (s.equalsIgnoreCase("BACK")) {
            throw new BackException();
        }
        return s;
    }

    //3.Asks one value. If a current value exists, pressing ENTER keeps it.
    private static String ask(String label, String current) {
        String shown = (current == null) ? "" : " (current: " + current + ")";
        String s = read(label + shown + ": ");
        if (s.isEmpty() && current != null) {
            return current;
        }
        return s;
    }

    private static boolean confirm(String question) {
        while (true) {
            String c = readRaw(question + " [1 Yes / 0 No]: ");
            if (c.equals("1")) {
                return true;
            }
            if (c.equals("0")) {
                return false;
            }
            System.out.println("! Enter 1 or 0.");
        }
    }

    private static void pause() {
        readRaw("Press ENTER to return to the menu...");
    }

    private static String askText(String name, String hint, String current, int min, int max, String uniqueKind) {
        while (true) {
            String in = ask(name + " [" + hint + "]", current);
            try {
                Registrable.checkLength(in, name, min, max);
                String value = in.trim();
                if (uniqueKind != null && !value.equalsIgnoreCase(current == null ? "" : current)
                        && isDuplicate(uniqueKind, value)) {
                    throw new DuplicateRecordException(name + " " + value
                            + " already exists. Please enter a different value.");
                }
                return value;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    private static String askEmail(String name, String current, String uniqueKind) {
        while (true) {
            String in = ask(name + " [String | email, e.g. name@auca.ac.rw]", current);
            try {
                Registrable.checkEmail(in, name);
                String value = in.trim();
                if (uniqueKind != null && !value.equalsIgnoreCase(current == null ? "" : current)
                        && isDuplicate(uniqueKind, value)) {
                    throw new DuplicateRecordException(name + " " + value
                            + " already exists. Please enter a different value.");
                }
                return value;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    private static String askPhone(String name, String current) {
        while (true) {
            String in = ask(name + " [String | 10-15 digits, optional leading +]", current);
            try {
                Registrable.checkPhone(in, name);
                return in.trim();
            } catch (ValidationException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    // mode: PAST, NOT_FUTURE or ANY. If 'after' is not null the date must be after it.
    private static String askDate(String name, String current, int mode, LocalDate after) {
        String rule = (mode == PAST) ? " | in the past" : (mode == NOT_FUTURE) ? " | not in the future" : "";
        while (true) {
            String in = ask(name + " [LocalDate | YYYY-MM-DD" + rule + "]", current);
            try {
                LocalDate d = LocalDate.parse(in);
                if (mode == PAST) {
                    Registrable.checkPastDates(d, name);
                }
                if (mode == NOT_FUTURE && d.isAfter(LocalDate.now())) {
                    throw new ValidationException(name + " must not be in the future");
                }
                if (after != null && !d.isAfter(after)) {
                    throw new ValidationException(name + " must be after " + after);
                }
                return d.toString();
            } catch (DateTimeParseException e) {
                System.out.println("! Invalid date: use YYYY-MM-DD (example: 2004-05-16). Please re-enter " + name + ".");
            } catch (ValidationException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    private static String askInt(String name, String current, int min, int max) {
        while (true) {
            String in = ask(name + " [int | " + min + "-" + max + "]", current);
            try {
                int n = Integer.parseInt(in);
                if (n < min || n > max) {
                    throw new ValidationException(name + " must be between " + min + " and " + max);
                }
                return String.valueOf(n);
            } catch (NumberFormatException e) {
                System.out.println("! Enter a whole number (no letters or decimals).");
            } catch (ValidationException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    private static String askDouble(String name, String current, String hint, double min, double max, boolean minExclusive) {
        while (true) {
            String in = ask(name + " [" + hint + "]", current);
            try {
                double n = Double.parseDouble(in);
                if (Double.isNaN(n) || Double.isInfinite(n) || n < min || n > max || (minExclusive && n == min)) {
                    throw new ValidationException(name + " is out of range (" + hint + ")");
                }
                return String.valueOf(n);
            } catch (NumberFormatException e) {
                System.out.println("! Enter a number, for example 7.5");
            } catch (ValidationException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    private static String askAmount(String name, String current) {
        while (true) {
            String in = ask(name + " [BigDecimal | greater than 0, e.g. 150000.50]", current);
            try {
                BigDecimal amount = new BigDecimal(in);
                if (amount.signum() <= 0) {
                    throw new ValidationException(name + " must be strictly positive");
                }
                return amount.toPlainString();
            } catch (NumberFormatException e) {
                System.out.println("! Invalid amount: enter digits only, for example 150000.50");
            } catch (ValidationException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    private static String askChoice(String name, String current, List<String> options) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < options.size(); i++) {
            if (i > 0) {
                sb.append(" / ");
            }
            sb.append(i + 1).append(" ").append(options.get(i));
        }
        while (true) {
            String in = ask(name + " [" + sb + "]", current);
            for (int i = 0; i < options.size(); i++) {
                if (in.equalsIgnoreCase(options.get(i)) || in.equals(String.valueOf(i + 1))) {
                    return options.get(i);
                }
            }
            System.out.println("! Choose one of: " + sb);
        }
    }

    // LISTS AND UNIQUENESS
    private static ArrayList<Identifiable> listFor(int kind, boolean activeOnly) {
        ArrayList<Identifiable> all = new ArrayList<>();
        switch (kind) {
            case DEPARTMENT -> all.addAll(departments);
            case STUDENT -> all.addAll(students);
            case TEACHER -> all.addAll(teachers);
            case ADMIN -> all.addAll(administrators);
            case COURSE -> all.addAll(courses);
            case CLASSROOM -> all.addAll(classrooms);
            case ENROLLMENT -> all.addAll(enrollments);
            case ASSESSMENT -> all.addAll(assessments);
            case PAYMENT -> all.addAll(payments);
            case BOOK -> all.addAll(books);
            case LOAN -> all.addAll(loans);
        }
        if (!activeOnly) {
            return all;
        }
        ArrayList<Identifiable> active = new ArrayList<>();
        for (Identifiable item : all) {
            if (item.isActive()) {
                active.add(item);
            }
        }
        return active;
    }

    private static ArrayList<Identifiable> allRecords() {
        ArrayList<Identifiable> all = new ArrayList<>();
        for (int kind = DEPARTMENT; kind <= LOAN; kind++) {
            all.addAll(listFor(kind, false));
        }
        return all;
    }

    private static ArrayList<Person> allPeople() {
        ArrayList<Person> people = new ArrayList<>();
        people.addAll(students);
        people.addAll(teachers);
        people.addAll(administrators);
        return people;
    }

    private static boolean isDuplicate(String kind, String value) {
        if (kind.equals("ID")) {
            for (Identifiable item : allRecords()) {
                if (item.getId().equalsIgnoreCase(value)) {
                    return true;
                }
            }
            return false;
        }
        if (kind.equals("EMAIL")) {
            for (Person p : allPeople()) {
                if (p.getEmail().equalsIgnoreCase(value)) {
                    return true;
                }
            }
            return false;
        }
        for (Student s : students) {
            if (kind.equals("MEMBER") && s.getMemberNumber().equalsIgnoreCase(value)) return true;
            if (kind.equals("INSTEMAIL") && s.getInstitutionalEmail().equalsIgnoreCase(value)) return true;
            if (kind.equals("STUDENTNO") && s.getStudentNumber().equalsIgnoreCase(value)) return true;
        }
        for (Teacher t : teachers) {
            if (kind.equals("MEMBER") && t.getMemberNumber().equalsIgnoreCase(value)) return true;
            if (kind.equals("INSTEMAIL") && t.getInstitutionalEmail().equalsIgnoreCase(value)) return true;
            if (kind.equals("EMPNO") && t.getEmployeeNumber().equalsIgnoreCase(value)) return true;
        }
        for (Administrator a : administrators) {
            if (kind.equals("STAFFNO") && a.getStaffNumber().equalsIgnoreCase(value)) return true;
        }
        for (Course c : courses) {
            if (kind.equals("COURSECODE") && c.getCourseCode().equalsIgnoreCase(value)) return true;
        }
        for (FeePayment p : payments) {
            if (kind.equals("RECEIPT") && p.getReceiptNumber().equalsIgnoreCase(value)) return true;
        }
        for (LibraryBook b : books) {
            if (kind.equals("ISBN") && b.getIsbn().equalsIgnoreCase(value)) return true;
        }
        return false;
    }

    private static void requireUnique(String kind, String value, String label) throws DuplicateRecordException {
        if (isDuplicate(kind, value)) {
            throw new DuplicateRecordException(label + " " + value + " already exists.");
        }
    }

    private static boolean activeEnrollmentExists(Student student, Course course, String semester, Enrollment skip) {
        for (Enrollment e : enrollments) {
            if (e != skip && e.getStudent() == student && e.getCourse() == course
                    && e.getSemester().equals(semester) && e.isActive()) {
                return true;
            }
        }
        return false;
    }

    private static final int DEPARTMENT = 1;
    private static final int STUDENT = 2;
    private static final int TEACHER = 3;
    private static final int ADMIN = 4;
    private static final int COURSE = 5;
    private static final int CLASSROOM = 6;
    private static final int ENROLLMENT = 7;
    private static final int ASSESSMENT = 8;
    private static final int PAYMENT = 9;
    private static final int BOOK = 10;
    private static final int LOAN = 11;

    private static final int PAST = 1;        // must be before today
    private static final int NOT_FUTURE = 2;  // today or earlier
    private static final int ANY = 3;         // no rule

    private static final String LINE = "+" + "-".repeat(60) + "+";

    private static final int MIN_ID = Identifiable.MIN_ID_LENGTH;
    private static final int MAX_ID = Identifiable.MAX_ID_LENGTH;
    private static final int MIN_NAME = Registrable.MIN_NAME_LENGTH;
    private static final int MAX_NAME = Registrable.MAX_NAME_LENGTH;
    private static final String ID_HINT = "String | unique | " + MIN_ID + "-" + MAX_ID + " chars";
    private static final String NAME_HINT = "String | " + MIN_NAME + "-" + MAX_NAME + " chars";

    private static final List<String> YES_NO = List.of("YES", "NO");

    private static final String[] TITLES = {"", "DEPARTMENT", "STUDENT", "TEACHER", "ADMINISTRATOR",
            "COURSE", "CLASSROOM", "ENROLLMENT", "ASSESSMENT", "FEE PAYMENT", "LIBRARY BOOK", "BOOK LOAN"};

    // field names
    private static final String[][] NAMES = {
            {},
            {"", "Department ID", "Department name", "Department code", "Office location", "Contact email", "Head teacher"},
            {"", "Person ID", "First name", "Last name", "Email", "Phone", "Date of birth",
                    "Member number", "Department", "Join date", "Academic status", "Institutional email", "Advisor name",
                    "Student number", "Program name", "Admission date", "Study year", "Guardian phone", "Scholarship"},
            {"", "Person ID", "First name", "Last name", "Email", "Phone", "Date of birth",
                    "Member number", "Department", "Join date", "Academic status", "Institutional email", "Advisor name",
                    "Employee number", "Specialization", "Hire date", "Salary", "Office number", "Employment type"},
            {"", "Person ID", "First name", "Last name", "Email", "Phone", "Date of birth",
                    "Staff number", "Job title", "Assigned office", "Employment date", "Access level", "Active"},
            {"", "Course ID", "Course code", "Course title", "Credit hours", "Department", "Assigned teacher"},
            {"", "Classroom ID", "Building name", "Room number", "Capacity", "Floor number", "Available"},
            {"", "Enrollment ID", "Student", "Course", "Enrollment date", "Semester", "Enrollment status"},
            {"", "Assessment ID", "Enrollment", "Assessment type", "Maximum score", "Obtained score", "Assessment date"},
            {"", "Payment ID", "Student", "Amount", "Payment date", "Payment method", "Receipt number"},
            {"", "Book ID", "ISBN", "Title", "Author", "Total copies", "Available copies"},
            {"", "Loan ID", "Student", "Library book", "Borrowed date", "Due date", "Returned date"}
    };

    private static final int[][] STEP_ENDS = {
            {}, {2, 4, 6}, {6, 12, 18}, {6, 12, 18}, {6, 9, 12},
            {2, 4, 6}, {2, 4, 6}, {2, 4, 6}, {2, 4, 6}, {2, 4, 6}, {2, 4, 6}, {2, 4, 6}
    };

    private static final String[][] STEP_NAMES = {
            {},
            {"Identity", "Location", "Contact and head teacher"},
            {"Personal details", "Academic details", "Student details"},
            {"Personal details", "Academic details", "Teacher details"},
            {"Personal details", "Job details", "Access details"},
            {"Identity", "Title and credits", "Department and teacher"},
            {"Identity and building", "Room and capacity", "Floor and availability"},
            {"Identity and student", "Course and date", "Semester and status"},
            {"Identity and enrollment", "Type and maximum score", "Result and date"},
            {"Identity and student", "Amount and date", "Method and receipt"},
            {"Identity", "Title and author", "Stock"},
            {"Loan and student", "Book and borrowed date", "Due and return"}
    };

}


