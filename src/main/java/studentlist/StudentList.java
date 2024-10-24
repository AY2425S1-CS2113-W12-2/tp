package studentlist;

import exceptions.SEPException;
import exceptions.SEPRangeException;
import exceptions.SEPNotFoundException;
import exceptions.SEPFormatException;
import exceptions.SEPDuplicateException;

import student.Student;
import ui.UI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 * Manages a list of students and provides methods for adding, deleting, sorting,
 * printing and validating student data such as student ID, GPA, and preferences.
 */
public class StudentList {
    public static final String ADD_STUDENT_REGEX = "^add\\s+id/[\\S ]+\\s+gpa/[\\S ]+\\s+p/\\{[\\S ]+}$";
    public static final String ID_REGEX = "^[A-Z]\\d{7}[A-Z]$";
    public static final String GPA_REGEX = "\\d+(\\.\\d{1,2})?";

    private ArrayList<Student> students;
    private UI ui;

    /**
     * Constructs a new StudentList with an empty list of students and the given UI object.
     *
     * @param ui The UI instance used to interact with the user.
     */
    public StudentList(UI ui) {
        this.students = new ArrayList<>();
        this.ui = ui;
    }

    /**
     * Copy constructor that creates a deep copy of another StudentList object.
     *
     * @param other The StudentList object to copy.
     */
    public StudentList(StudentList other) {
        this.students = new ArrayList<>(other.students.size());
        for (Student student : other.students) {
            this.students.add(new Student(student));
        }
        this.ui = other.ui;
    }

    public ArrayList<Student> getList() {
        return students;
    }

    /**
     * Adds a new student to the list if the student does not already exist.
     *
     * @param student The Student object to add.
     * @throws SEPDuplicateException If a student with the same ID already exists in the list.
     */
    public void addStudent(Student student) throws SEPDuplicateException {
        for (Student existingStudent : students) {
            if (existingStudent.getId().equals(student.getId())) {
                throw SEPDuplicateException.rejectDuplicateStudent();
            }
        }
        students.add(student);
    }

    /**
     * Deletes a student from the list by their ID.
     *
     * @param input The input that includes the ID of the student to delete.
     * @throws SEPException If the student ID is not found or the ID format is invalid.
     */
    public void deleteStudent(String input) throws SEPException{
        String[] parts = input.split("delete", 2);
        String studentId = organiseId(parts[1]);
        if (!studentId.matches(ID_REGEX)) {
            throw SEPFormatException.rejectIdFormat();
        }
        boolean isRemoved = students.removeIf(student -> student.getId().equals(studentId));
        if (!isRemoved) {
            throw SEPNotFoundException.rejectStudentNotFound();
        }
    }

    /**
     * Sorts the student list by GPA in descending order.
     */
    public void sortStudentsByGPA() {
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return Float.compare(s2.getGpa(), s1.getGpa());
            }
        });
    }

    /**
     * Sorts the student list by student ID in ascending order.
     */
    public void sortStudentsById() {
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return s1.getId().compareTo(s2.getId());
            }
        });
    }

    public int getNumStudents() {
        return students.size();
    }

    /**
     * Handles the creation of a Student object by validating the input for Student ID, GPA, and preferences.
     *
     * @param input The input string containing student information.
     * @return The created Student object if all validations pass.
     * @throws SEPException If any validation errors occur, a SEPException containing all error messages is thrown.
     */
    public Student makeStudent(String input) throws SEPException {
        Set<String> errorMessages = new HashSet<>();

        String[] parts = splitInput(input);

        String studentId = organiseId(parts[1]);
        validateStudentId(studentId, errorMessages);

        float gpa = validateGpa(parts[2].trim(), errorMessages);

        ArrayList<Integer> preferences = validatePreferences(parts[3].trim(), errorMessages);

        // If any errors occurred, throw a single exception with all error messages
        if (!errorMessages.isEmpty()) {
            throw new SEPException(String.join("\n", errorMessages));
        }

        return new Student(studentId, gpa, preferences);
    }

    public void generateReport() {
        try {
            ui.generateReport(students);
        } catch (SEPException e) {
            ui.printResponse(e.getMessage());
        }
    }

    /**
     * Updates the current student list with a new one.
     *
     * @param studentList The new student list to set.
     */
    public void setStudentList(StudentList studentList) {
        this.students = studentList.students; // Overwrite the current ArrayList
    }

    /**
     * Finds a student in the student list or report by their student ID.
     * Keywords inputted need not be a valid student ID,
     * but rather keywords that the student ID contains.
     * Note: find keyword input is case-sensitive.
     *
     * @param input The ID or keywords of the student to find.
     * @throws SEPException If student find format inputted is wrong, or if student id cannot be found.
     */
    public void findStudent(String input) throws SEPException {
        String[] parts = input.split("\\s+", 3);
        ArrayList<Student> foundStudent = new ArrayList<>();
        if (parts.length != 3) {
            throw SEPFormatException.rejectFindFormat();
        }

        assert parts.length == 3;
        String command = parts[1].trim().toLowerCase();
        String studentId = parts[2].trim();
        for (Student student : students) {
            if (student.getId().contains(studentId)) {
                foundStudent.add(student);
            }
        }

        switch (command) {
        case "list":
            if (foundStudent.isEmpty()) {
                throw SEPNotFoundException.rejectStudentNotFound();
            }
            ui.printResponse("Finding for students... student(s) found.");
            ui.printStudentList(foundStudent);
            break;
        case "report":
            if (foundStudent.isEmpty()) {
                throw SEPNotFoundException.rejectStudentNotFound();
            }
            ui.printResponse("Finding for students... student(s) found.");
            ui.generateReport(foundStudent);
            break;
        default:
            throw SEPFormatException.rejectFindFormat();
        }
    }

    /**
     * Organizes the student ID by removing all spaces,
     * including any spaces in between the numbers or alphabets,
     * before validating the student ID.
     *
     * @param id The raw student ID string.
     * @return A trimmed and space-free student ID.
     */
    private String organiseId(String id) {
        String studentId = id.trim();
        return studentId.replaceAll("\\s+", "");
    }

    /**
     * Splits the input string into parts based on the expected format
     * and order: id/, gpa/, followed by p/. Any other order would be
     * rejected and a format exception would be thrown.
     *
     * @param input The raw input string.
     * @return A String array containing the parts of the input.
     * @throws SEPFormatException If the input format or order is invalid.
     */
    private String[] splitInput(String input) throws SEPException {
        if (!input.matches(ADD_STUDENT_REGEX)) {
            throw SEPFormatException.rejectAddStudentFormat();
        }
        String[] parts = input.split("id/|gpa/|p/");
        if (parts.length != 4) {
            throw SEPFormatException.rejectAddStudentFormat();
        }
        return parts;
    }

    /**
     * Validates the student ID format:
     * 1 alphabet, followed by 7 digits, then another alphabet.
     * Alphabets must be in uppercase.
     *
     * @param studentId The student ID to validate.
     * @param errorMessages A set to collect error messages.
     */
    public void validateStudentId(String studentId, Set<String> errorMessages) {
        if (!studentId.matches(ID_REGEX)) {
            errorMessages.add(SEPFormatException.rejectIdFormat().getMessage());
        }
    }

    /**
     * Filters students based on their university preferences.
     *
     * @param uniIndex The index of the university to filter students by.
     * @return A list of students who have the specified university index in their preferences.
     * @throws SEPException if no students have chosen the specified university.
     */
    public List<Student> getStudentsByUniversityIndex(int uniIndex) throws SEPException {
        List<Student> filteredStudents = new ArrayList<>();
        for (Student student : this.students) {
            if (student.getUniPreferences().contains(uniIndex)) {
                filteredStudents.add(student);
            }
        }
        if (filteredStudents.isEmpty()) {
            throw new SEPException("No students have chosen this university.");
        }
        return filteredStudents;
    }

    /**
     * Calculates the average GPA of students who chose the specified university.
     *
     * @param uniIndex The index of the university.
     * @return The average GPA as a double.
     * @throws SEPException if no students have chosen the specified university.
     */
    public double calculateAverageGpaForUniversity(int uniIndex) throws SEPException {
        List<Student> students = getStudentsByUniversityIndex(uniIndex);
        return students.stream()
                       .mapToDouble(Student::getGpa)
                       .average()
                       .orElse(0.0);
    }

    /**
     * Calculates the minimum GPA of students who chose the specified university.
     *
     * @param uniIndex The index of the university.
     * @return The minimum GPA as a double.
     * @throws SEPException if no students have chosen the specified university.
     */
    public double calculateMinGpaForUniversity(int uniIndex) throws SEPException {
        List<Student> students = getStudentsByUniversityIndex(uniIndex);
        return students.stream()
                       .mapToDouble(Student::getGpa)
                       .min()
                       .orElse(0.0);
    }

    /**
     * Validates the GPA format and range.
     * GPA should be a float between 1 and 5, with a maximum of 2 decimal places.
     *
     * @param gpaStr The GPA string to validate.
     * @param errorMessages A set to collect error messages.
     * @return A float representing the valid GPA.
     */
    public float validateGpa(String gpaStr, Set<String> errorMessages) {
        float gpa = 0.0f;
        try {
            if (!gpaStr.matches(GPA_REGEX)) {
                throw new NumberFormatException();
            }
            gpa = Float.parseFloat(gpaStr);
            if (gpa < 0.0 || gpa > 5.0) {
                errorMessages.add(SEPRangeException.rejectGpaRange().getMessage());
            }
        } catch (NumberFormatException e) {
            errorMessages.add(SEPFormatException.rejectGpaFormat().getMessage());
        }
        return gpa;
    }

    /**
     * Validates the student preferences for valid range and format.
     * Preference rankings should be wrapped in curly braces and have up to 3 rankings.
     *
     * @param preferencesData The preference string to validate.
     * @param errorMessages A set to collect error messages.
     * @return A list of valid preferences.
     */
    public ArrayList<Integer> validatePreferences(String preferencesData, Set<String> errorMessages) {
        ArrayList<Integer> preferences = new ArrayList<>();
        String[] numberStrings = preferencesData.replaceAll("[{}]", "").split(",");

        if (numberStrings.length > 3 || numberStrings[0].trim().isEmpty()) {
            errorMessages.add(SEPRangeException.rejectRankingsRange().getMessage());
        }

        for (String numberString : numberStrings) {
            try {
                int preference = Integer.parseInt(numberString.trim());
                if (preference < 1 || preference > 92) {
                    errorMessages.add(SEPRangeException.rejectPreferenceRange().getMessage());
                } else {
                    preferences.add(preference);
                }
            } catch (NumberFormatException e) {
                errorMessages.add(SEPFormatException.rejectPreferenceFormat().getMessage());
            }
        }

        return preferences;
    }
}
