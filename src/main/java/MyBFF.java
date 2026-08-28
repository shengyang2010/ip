import java.util.Scanner;

/**
 * Runs the MyBFF chatbot.
 */
public class MyBFF {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final int MAX_TASKS = 100;

    /**
     * Greets the user, stores tasks in memory, and handles the supported commands.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String banner =
                " __  ____   ______  ______ ______ \n"
                        + "|  \\/  \\ \\ / /  _ \\|  ____|  ____|\n"
                        + "| \\  / |\\ V /| |_) | |__  | |__   \n"
                        + "| |\\/| | | | |  _ <|  __| |  __|  \n"
                        + "| |  | | |.| | |_) | |    | |     \n"
                        + "|_|  |_| |_| |____/|_|    |_|     \n";

        System.out.println(SEPARATOR);
        System.out.println(banner);
        System.out.println("Hello! I'm MyBFF.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(SEPARATOR);

            if (command.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            }

            if (command.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + ".[" + tasks[i].getStatusIcon() + "] "
                            + tasks[i].getDescription());
                }
            } else if (command.startsWith("mark ")) {
                String taskNumber = command.substring("mark ".length()).trim();
                try {
                    int index = Integer.parseInt(taskNumber) - 1;
                    if (index >= 0 && index < taskCount) {
                        tasks[index].markAsDone();
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("   [X] " + tasks[index].getDescription());
                    } else {
                        System.out.println(" Invalid task number.");
                    }
                } catch (NumberFormatException exception) {
                    System.out.println(" Invalid task number.");
                }
            } else if (command.startsWith("unmark ")) {
                String taskNumber = command.substring("unmark ".length()).trim();
                try {
                    int index = Integer.parseInt(taskNumber) - 1;
                    if (index >= 0 && index < taskCount) {
                        tasks[index].markAsNotDone();
                        System.out.println(" OK, I've marked this task as not done yet:");
                        System.out.println("   [ ] " + tasks[index].getDescription());
                    } else {
                        System.out.println(" Invalid task number.");
                    }
                } catch (NumberFormatException exception) {
                    System.out.println(" Invalid task number.");
                }
            } else if (taskCount < MAX_TASKS) {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println(" added: " + command);
            } else {
                System.out.println(" Your task list is full.");
            }
            System.out.println(SEPARATOR);
            System.out.println();
        }
        scanner.close();
    }
}
