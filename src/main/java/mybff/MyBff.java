package mybff;

import java.util.Scanner;

/**
 * Runs the MyBFF chatbot.
 */
public class MyBff {
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final int MAX_TASKS = 100;
    private static final String BANNER =
            " __  ____   ______  ______ ______ \n"
                    + "|  \\/  \\ \\ / /  _ \\|  ____|  ____|\n"
                    + "| \\  / |\\ V /| |_) | |__  | |__   \n"
                    + "| |\\/| | | | |  _ <|  __| |  __|  \n"
                    + "| |  | | |.| | |_) | |    | |     \n"
                    + "|_|  |_| |_| |____/|_|    |_|     \n";

    /**
     * Greets the user, stores tasks in memory, and handles the supported commands.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        printGreeting();
        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(SEPARATOR);

            if (command.equals(COMMAND_BYE)) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            }

            if (command.equals(COMMAND_LIST)) {
                printTaskList(tasks, taskCount);
            } else if (command.startsWith(COMMAND_MARK)) {
                processCompletionCommand(command, COMMAND_MARK, tasks, taskCount, true);
            } else if (command.startsWith(COMMAND_UNMARK)) {
                processCompletionCommand(command, COMMAND_UNMARK, tasks, taskCount, false);
            } else {
                taskCount = addTask(tasks, taskCount, command);
            }
            System.out.println(SEPARATOR);
            System.out.println();
        }
        scanner.close();
    }

    /** Prints the chatbot banner and greeting. */
    private static void printGreeting() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println("Hello! I'm MyBff.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
        System.out.println();
    }

    /** Prints all stored tasks with their indexes and completion statuses. */
    private static void printTaskList(Task[] tasks, int taskCount) {
        for (int i = 0; i < taskCount; i++) {
            System.out.println(" " + (i + 1) + ".[" + tasks[i].getStatusIcon() + "] "
                    + tasks[i].getDescription());
        }
    }

    /**
     * Adds a task when the task list has capacity.
     *
     * @return the updated number of tasks
     */
    private static int addTask(Task[] tasks, int taskCount, String description) {
        if (taskCount >= MAX_TASKS) {
            System.out.println(" Your task list is full.");
            return taskCount;
        }

        tasks[taskCount] = new Task(description);
        System.out.println(" added: " + description);
        return taskCount + 1;
    }

    /** Processes a command that changes a task's completion status. */
    private static void processCompletionCommand(String command, String commandPrefix,
            Task[] tasks, int taskCount, boolean isMarkingAsDone) {
        String taskNumber = command.substring(commandPrefix.length()).trim();
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (index >= 0 && index < taskCount) {
                if (isMarkingAsDone) {
                    tasks[index].markAsDone();
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   [X] " + tasks[index].getDescription());
                } else {
                    tasks[index].markAsNotDone();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   [ ] " + tasks[index].getDescription());
                }
            } else {
                System.out.println(" Invalid task number.");
            }
        } catch (NumberFormatException exception) {
            System.out.println(" Invalid task number.");
        }
    }
}
