package command;

import exceptions.SEPException;

import studentlist.StudentList;
import ui.UI;

public class FindCommand extends Command {
    private String input;
    private UI ui;

    public FindCommand(StudentList studentList, String input, UI ui) {
        super(studentList);
        this.input = input;
        this.ui = ui;
    }

    @Override
    public void run() {
        try {
            super.studentList.findStudent(this.input);;
        } catch (SEPException e) {
            ui.printResponse(e.getMessage());
        }
    }
}
