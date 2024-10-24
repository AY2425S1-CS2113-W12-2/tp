package studentlist;

import exceptions.SEPDuplicateException;
import exceptions.SEPException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import student.Student;
import ui.UI;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StudentListTest {
    private StudentList studentList;
    private ArrayList<Integer> uniPreferences = new ArrayList<>(Arrays.asList(1,2,3));
    private Student bob = new Student("A1234567K", (float)4.6, uniPreferences);

    @BeforeEach
    public void setUp() {
        this.studentList = new StudentList(new UI());
    }

    @Test
    public void addStudent_duplicateStudent_exceptionThrown() {
        assertDoesNotThrow(() -> this.studentList.addStudent(this.bob));
        assertThrows(SEPDuplicateException.class, () -> this.studentList.addStudent(this.bob));
    }

    @Test
    public void testAddStudent() {
        assertDoesNotThrow(() -> this.studentList.addStudent(this.bob));

        assertEquals(1, this.studentList.getNumStudents());

        assertEquals("A1234567K", studentList.getList().get(0).getId());
    }

    @Test
    public void testDeleteStudent() {
        assertDoesNotThrow(() -> this.studentList.addStudent(this.bob));
        assertEquals(1, studentList.getNumStudents());

        assertDoesNotThrow(() -> this.studentList.deleteStudent("delete A1234567K"));
        assertEquals(0, studentList.getNumStudents());
    }

    @Test
    public void testSortStudentsByGPA() {
        Student student1 = new Student("A1234567B", 3.2f, new ArrayList<>());
        Student student2 = new Student("A2345678C", 4.8f, new ArrayList<>());
        Student student3 = new Student("A3456789D", 3.9f, new ArrayList<>());

        assertDoesNotThrow(() -> this.studentList.addStudent(student1));
        assertDoesNotThrow(() -> this.studentList.addStudent(student2));
        assertDoesNotThrow(() -> this.studentList.addStudent(student3));

        studentList.sortStudentsByGPA();

        assertEquals("A2345678C", studentList.getList().get(0).getId()); // Highest GPA first
        assertEquals("A1234567B", studentList.getList().get(2).getId()); // Lowest GPA last
    }

    @Test
    public void testSortStudentsById() {
        Student student1 = new Student("A1234567B", 3.2f, new ArrayList<>());
        Student student2 = new Student("A2345678C", 4.8f, new ArrayList<>());
        Student student3 = new Student("A3456789D", 3.9f, new ArrayList<>());

        assertDoesNotThrow(() -> this.studentList.addStudent(student1));
        assertDoesNotThrow(() -> this.studentList.addStudent(student2));
        assertDoesNotThrow(() -> this.studentList.addStudent(student3));

        studentList.sortStudentsById();

        assertEquals("A1234567B", studentList.getList().get(0).getId()); // Lexicographical order
        assertEquals("A3456789D", studentList.getList().get(2).getId());
    }

    @Test
    public void testMakeStudent() {
        String input = "add id/A1234567B gpa/4.5 p/{1,2,3}";
        Student student = assertDoesNotThrow(() -> this.studentList.makeStudent(input));

        assertNotNull(student);
        assertEquals("A1234567B", student.getId());
        assertEquals(4.5f, student.getGpa());
        assertEquals(3, student.getUniPreferences().size());
    }

    @Test
    public void makeStudent_idWithSpaces_studentCreated() {
        String input = "add id/A55 345 67B gpa/3.99 p/{1,2,3}"; //ID contains spaces, but is handled
        Student student = assertDoesNotThrow(() -> this.studentList.makeStudent(input));

        assertNotNull(student);
        assertEquals("A5534567B", student.getId());
        assertEquals(3.99f, student.getGpa());
        assertEquals(3, student.getUniPreferences().size());
    }

    @Test
    public void makeStudent_invalidAddOrder_exceptionThrown() {
        String input = "add id/A1234567B p/{1,2,3} gpa/4.5"; //Invalid add order

        assertThrows(SEPException.class, () -> this.studentList.makeStudent(input));
    }

    @Test
    public void makeStudent_invalidAddFormat_exceptionThrown() {
        String input = "add id(A1234567B) gpa(4.5) p({1,2,3})"; //Invalid add format

        assertThrows(SEPException.class, () -> this.studentList.makeStudent(input));
    }

    @Test
    public void makeStudent_invalidIDFormat_exceptionThrown() {
        String input = "add id/1234567A gpa/4.5 p/{1,2,3}";  // Invalid ID format (missing letter at start)

        assertThrows(SEPException.class, () -> this.studentList.makeStudent(input));
    }

    @Test
    public void makeStudent_invalidGPAFormat_exceptionThrown() {
        String input = "add id/A1234567B gpa/invalid p/{1,2,3}";  // Invalid GPA format

        assertThrows(SEPException.class, () -> this.studentList.makeStudent(input));
    }

    @Test
    public void makeStudent_invalidGPADecimalPlaces_exceptionThrown() {
        String input = "add id/A1234567B gpa/3.999 p/{1,2,3}";  // Invalid GPA, more than 2 decimal places

        assertThrows(SEPException.class, () -> this.studentList.makeStudent(input));
    }

    @Test
    public void makeStudent_invalidPreferencesFormat_exceptionThrown() {
        String input = "add id/A1234567B gpa/4.5 p/1,2,3"; // Missing curly braces around preferences

        assertThrows(SEPException.class, () -> this.studentList.makeStudent(input));
    }

    @Test
    public void makeStudent_invalidGPARange_exceptionThrown() {
        String input = "add id/A1234567B gpa/6.0 p/{1,2,3}";  // GPA out of range

        assertThrows(SEPException.class, () -> this.studentList.makeStudent(input));
    }

    @Test
    public void makeStudent_preferencesOutOfRange_exceptionThrown() {
        String input = "add id/A1234567B gpa/4.5 p/{0,4,95}"; // Preferences outside valid range

        assertThrows(SEPException.class, () -> this.studentList.makeStudent(input));
    }

    @Test
    public void deleteStudent_nonExistentStudent_exceptionThrown() {
        assertDoesNotThrow(() -> this.studentList.addStudent(this.bob));
        assertEquals(1, this.studentList.getNumStudents());

        // Test deleting a student that doesn't exist
        assertThrows(SEPException.class, () -> this.studentList.deleteStudent("delete B7654321C"));
    }

    @Test
    public void findStudent_nonExistentStudent_exceptionThrown() {
        assertDoesNotThrow(() -> this.studentList.addStudent(this.bob));
        assertEquals(1, this.studentList.getNumStudents());

        // Test finding a student that doesn't exist
        assertThrows(SEPException.class, () -> this.studentList.findStudent("find list B7654321C"));
    }

    @Test
    public void findStudent_invalidFindFormat_exceptionThrown() {
        String input = "find A1234567I";  // Invalid Find format (Missing 'list' or 'report')

        assertThrows(SEPException.class, () -> this.studentList.findStudent(input));
    }
}
