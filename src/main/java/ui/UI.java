package ui;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

import student.Student;
import university.University;
import university.UniversityRepository;

/**
 * Handles user interface FindMySEP application.
 * Methods to print messages, retrieve user input, display ASCII table
 * for student data.
 */
public class UI {
    private static final int LINE_LENGTH = 80;
    private static final String HORIZONTAL_LINE = "-".repeat(LINE_LENGTH);
    private final Scanner scanner;

    /**
     * Constructor for UI class
     */
    public UI() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prints the given item to the console, wrapped by horizontal lines above
     * and below the specified item.
     *
     * @param item The item to be printed, which can be of any type.
     *             If the item is null, it prints "null".
     */
    public <T> void printResponse(T item) {
        String content = (item == null) ? "null" : item.toString();
        System.out.println(HORIZONTAL_LINE + "\n" + content + "\n" + HORIZONTAL_LINE);
    }

    /**
     * Retrieves a line of user input from the console.
     *
     * @return The line of input entered by the user, or an empty string
     *         if an error occurs while reading input.
     */
    public String getUserInput() {
        try {
            return scanner.nextLine();
        } catch (Exception e) {
            printResponse("Error reading input: " + e.getMessage());
            return ""; // Return an empty string
        }
    }

    /**
     * Prints a greeting message to the console.
     */
    public void sayHi() {
        printResponse(Messages.WELCOME);
    }

    /**
     * Prints a farewell message to the console.
     */
    public void cleanupAndExit() {
        printResponse("Adios, amigo!");
        scanner.close();
    }

    /**
     * Prints table displaying student information.
     * The table includes student IDs, GPAs, and preference rankings.
     *
     * @param studentList ArrayList of Student objects to be printed
     */
    public void printStudentList(ArrayList<Student> studentList) {
        if (studentList == null || studentList.isEmpty()) {
            printResponse("No students available for report generation.");
            return;
        }

        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Student", "GPA", "Preference Rankings");
        at.addRule();

        for (Student s : studentList) {
            // Using Java Streams to create a comma-separated preference rankings string
            String uniPrefsString = s.getUniPreferences().stream()
                    .map(String::valueOf) // Convert each Integer to String
                    .collect(Collectors.joining(",")); // Join them with commas

            // Use the uniPrefsString for adding to the table
            at.addRow(s.getId(), s.getGpa(), uniPrefsString);
        }
        at.addRule();

        at.setTextAlignment(TextAlignment.CENTER);
        at.setPaddingLeft(3);
        at.setPaddingRight(3);

        at.getRenderer().setCWC(new CWC_LongestLine());
        printResponse("Here is the list:\n" + at.render());
    }

    /**
     * Prints a buffering message when the algorithm is running.
     * Upon algorithm completion, replaces the buffering message with a
     * completed message.
     */
    public void printAllocatingMessage() {
        System.out.println(HORIZONTAL_LINE);
        try {
            System.out.print("\rLoading");
            while (!Thread.currentThread().isInterrupted()) { // Keep printing until interrupted
                for (int i = 0; i <= 3; i++) {
                    System.out.print("\rLoading" + ".".repeat(i));
                    Thread.sleep(500); // 500 milliseconds
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
        } finally {
            System.out.println("\r" + Messages.ALLOCATE_COMPLETE);
            System.out.println(HORIZONTAL_LINE);
        }
    }

    /**
     * Prints table displaying allocation results.
     * The table includes student IDs and respective allocation outcomes.
     *
     * @param studentList ArrayList of Student objects to be printed
     */
    public void generateReport(ArrayList<Student> studentList) {
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Student", "University Granted");
        at.addRule();

        for (Student s : studentList) {
            University allocated = UniversityRepository.getUniversityByIndex(s.getAllocatedUniversity());

            if (allocated != null) {
                at.addRow(s.getId(), allocated.getFullName());
            } else {
                at.addRow(s.getId(), "Not Allocated"); // Handle cases with no allocation
            }
        }
        at.addRule();

        printResponse("Here is the allocation report:\n" + at.render());
    }
}
