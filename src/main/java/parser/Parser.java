package parser;

import command.AddCommand;
import command.AllocateCommand;
import command.CriteriaCommand;
import command.DeleteCommand;
import command.ExitCommand;
import command.GenerateCommand;
import command.HelpCommand;
import command.ListCommand;
import command.StatCommand;
import command.UnknownCommand;
import command.ViewQuotaCommand;
import command.FindCommand;

import studentlist.StudentList;
import ui.UI;

import java.util.HashSet;
import java.util.Set;

/**
 * The Parser class is the main class responsible for parsing the user's input and executing the corresponding command.
 */
public class Parser {
    private StudentList studentList;
    private UI ui;

    public Parser(StudentList studentList, UI ui) {
        this.studentList = studentList;
        this.ui = ui;
    }

    /**
     * Parses the user's input and execute the corresponding command.
     * 
     * @return false if the user wants to exit the application, true otherwise.
     */
    public boolean parseInput(String input) {
        String[] parts = input.split(" ");
        switch (parts[0].toLowerCase()) {
        case "add":
            new AddCommand(this.studentList, input, this.ui).run();
            break;
        case "delete":
            new DeleteCommand(this.studentList, input, this.ui).run();
            break;
        case "find":
            new FindCommand(this.studentList, input, this.ui).run();
            break;
        case "list":
            new ListCommand(this.studentList, this.ui).run();
            break;
        case "allocate":
            new AllocateCommand(this.studentList, this.ui).run();
            break;
        case "exit":
        case "quit":
        case "bye":
            new ExitCommand(this.studentList, this.ui).run();
            return false;
        case "help":
            new HelpCommand(this.studentList, this.ui).run();
            break;
        case "generate":
            new GenerateCommand(this.studentList, this.ui).run();
            break;
        case "minimum":
            new CriteriaCommand(this.studentList, input, this.ui).run();
            break;
        case "stats":
            new StatCommand(this.studentList, input, this.ui).run();
            break;
        case "viewquota":
            new ViewQuotaCommand(this.studentList, input, this.ui).run();
            break;
        default:
            new UnknownCommand(this.studentList, this.ui).run();
            break;
        }
        return true;
    }

    /**
     * Validates the student data including ID, GPA, and preferences.
     *
     * @param id The student ID to validate.
     * @param gpa The GPA to validate.
     * @param preferences The preferences to validate.
     * @return true if all data is valid, false if there are validation errors.
     */
    public boolean isValidData(String id, String gpa, String preferences) {
        Set<String> errorMessages = new HashSet<>();
        this.studentList.validateStudentId(id, errorMessages);
        this.studentList.validateGpa(gpa, errorMessages);
        this.studentList.validatePreferences(preferences, errorMessages);
        return errorMessages.isEmpty();
    }
}
