package ui;

public enum Messages {
    WELCOME("Hi! Welcome to FindOurSEP!"),
    EXIT("Goodbye! Have a great day!"),
    ALLOCATE_COMPLETE("Allocation algorithm complete! Run generate command to view."),
    HELP("""
        Here is the list of possible commands:
            add         Adds a student with the specified ID, GPA, and preference rankings.
                        Example: add id/A1234567I gpa/5.0 p/{13,61,43}
                        Note: PREFERENCE_RANKINGS should be enclosed in curly braces

            delete      Deletes the student with the specified ID from the list.
                        Example: delete A1234567I

            find        Finds the student with the keyword, returning either a list or report.
                        Example: find list A1234567I

            list        Lists all students currently in the system.

            stats       Displays GPA statistics for students who have chosen a specific partner university.
                        Usage:
                        stats -avggpa <UNI_INDEX>  Displays the average GPA for the specified university.
                        stats -mingpa <UNI_INDEX>  Displays the minimum GPA for the specified university.

            viewQuota   Displays the index, name, and remaining quota for the specified university.
                        Usage: viewQuota <UNI_INDEX>

            allocate    Allocates students to available slots based on their preferences.

            generate    Generates a report of allocations and student data.

            exit        Exits the application.

            help        Displays this help message.
        """),
    ERROR("An error occurred. Please try again.");

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
