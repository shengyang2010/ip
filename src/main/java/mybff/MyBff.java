package mybff;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Runs the MyBFF chatbot.
 */
public class MyBff {
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final int MAX_TASKS = 100;
    private static final String STORAGE_HEADER = "MyBff storage v3";
    private static final Path TASK_FILE = Path.of("data", "mybff.txt");
    private static final String BANNER =
            "     __  ____   ______  ______ ______ \n"
                    + "     |  \\/  \\ \\ / /  _ \\|  ____|  ____|\n"
                    + "     | \\  / |\\ V /| |_) | |__  | |__   \n"
                    + "     | |\\/| | | | |  _ <|  __| |  __|  \n"
                    + "     | |  | | |.| | |_) | |    | |     \n"
                    + "     |_|  |_| |_| |____/|_|    |_|     \n";

    /**
     * Greets the user, stores tasks in memory, and handles the supported commands.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        printGreeting();
        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount;
        try {
            taskCount = loadTasks(tasks);
        } catch (IOException | SecurityException exception) {
            System.out.println("     OOPS!!! Could not load data/mybff.txt. "
                    + "Your saved file has not been changed. Check the file and restart.");
            scanner.close();
            return;
        }
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            String commandWord = command.split("\\s+", 2)[0];
            System.out.println("    " + SEPARATOR);

            if (command.equals(COMMAND_BYE)) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println("   " + SEPARATOR);
                break;
            }

            try {
                if (command.equals(COMMAND_LIST)) {
                    printTaskList(tasks, taskCount);
                } else if (commandWord.equals(COMMAND_MARK)) {
                    processCompletionCommand(command, COMMAND_MARK, tasks, taskCount, true);
                } else if (commandWord.equals(COMMAND_UNMARK)) {
                    processCompletionCommand(command, COMMAND_UNMARK, tasks, taskCount, false);
                } else {
                    try {
                        taskCount = addTask(tasks, taskCount, command);
                    } catch (MyBffException exception) {
                        System.out.println("     OOPS!!! " + exception.getMessage());
                    }
                }
            } catch (IOException | SecurityException exception) {
                System.out.println("     OOPS!!! Could not save data/mybff.txt. "
                        + "No changes were made. Check file access and try again.");
            }
            System.out.println("    " + SEPARATOR);
            System.out.println();
        }
        scanner.close();
    }

    /** Prints the chatbot banner and greeting. */
    private static void printGreeting() {
        System.out.println("    " + SEPARATOR);
        System.out.println(BANNER);
        System.out.println("     Hello! I'm MyBff.");
        System.out.println("     What can I do for you?");
        System.out.println("    " + SEPARATOR);
        System.out.println();
    }

    /** Prints all stored tasks with their indexes and completion statuses. */
    private static void printTaskList(Task[] tasks, int taskCount) {
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println("     " + (i + 1) + "." + tasks[i]);
        }
    }

    /**
     * Adds a task when the task list has capacity.
     *
     * @return the updated number of tasks
     * @throws MyBffException if the command is unknown or the todo description is empty
     */
    private static int addTask(Task[] tasks, int taskCount, String command)
            throws MyBffException, IOException {
        Task task = createTask(command);
        if (taskCount >= MAX_TASKS) {
            System.out.println(" Your task list is full.");
            return taskCount;
        }

        tasks[taskCount] = task;
        try {
            saveTasks(tasks, taskCount + 1);
        } catch (IOException | SecurityException exception) {
            tasks[taskCount] = null;
            throw exception;
        }
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + tasks[taskCount]);
        System.out.println("     Now you have " + (taskCount + 1) + " tasks in the list.");
        return taskCount + 1;
    }

    /**
     * Parses a task, rejecting empty todos and unknown commands.
     *
     * @throws MyBffException if the command is unknown or the todo description is empty
     */
    private static Task createTask(String command) throws MyBffException {
        if (command.startsWith(COMMAND_DEADLINE + " ")) {
            String[] deadlineParts = command.substring(COMMAND_DEADLINE.length())
                    .trim().split(" /by ", 2);
            return new Deadline(deadlineParts[0], deadlineParts.length == 2 ? deadlineParts[1] : "");
        }
        if (command.startsWith(COMMAND_EVENT + " ")) {
            String[] eventParts = command.substring(COMMAND_EVENT.length())
                    .trim().split(" /from ", 2);
            String[] timeParts = (eventParts.length == 2 ? eventParts[1] : "").split(" /to ", 2);
            return new Event(eventParts[0], timeParts[0], timeParts.length == 2 ? timeParts[1] : "");
        }
        String[] commandParts = command.split("\\s+", 2);
        if (commandParts[0].equals(COMMAND_TODO)) {
            String description = commandParts.length == 2 ? commandParts[1].trim() : "";
            if (description.isEmpty()) {
                throw new MyBffException("The description of a todo cannot be empty.");
            }
            return new ToDo(description);
        }
        throw new MyBffException("I'm sorry, but I don't know what that means :-(");
    }

    /** Processes a command that changes a task's completion status. */
    private static void processCompletionCommand(String command, String commandPrefix,
            Task[] tasks, int taskCount, boolean isMarkingAsDone) throws IOException {
        String taskNumber = command.substring(commandPrefix.length()).trim();
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (index >= 0 && index < taskCount) {
                boolean wasDone = tasks[index].getStatusIcon().equals("X");
                if (isMarkingAsDone) {
                    tasks[index].markAsDone();
                } else {
                    tasks[index].markAsNotDone();
                }
                try {
                    saveTasks(tasks, taskCount);
                } catch (IOException | SecurityException exception) {
                    if (wasDone) {
                        tasks[index].markAsDone();
                    } else {
                        tasks[index].markAsNotDone();
                    }
                    throw exception;
                }
                System.out.println(isMarkingAsDone ? "     Nice! I've marked this task as done:"
                        : "     OK, I've marked this task as not done yet:");
                System.out.println("       [" + tasks[index].getStatusIcon() + "] " + tasks[index].getDescription());
            } else {
                System.out.println("     Invalid task number.");
            }
        } catch (NumberFormatException exception) {
            System.out.println("     Invalid task number.");
        }
    }

    /** Loads saved tasks in order, or returns an empty list on the first run. */
    private static int loadTasks(Task[] tasks) throws IOException {
        if (Files.notExists(TASK_FILE)) {
            return 0;
        }
        int taskCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(TASK_FILE)) {
            String line = reader.readLine();
            if (line != null && line.startsWith("\uFEFF")) {
                line = line.substring(1);
            }
            boolean isEncoded = "MyBff storage v2".equals(line);
            boolean isEscaped = STORAGE_HEADER.equals(line);
            if (isEncoded || isEscaped) {
                line = reader.readLine();
            }
            while (line != null) {
                if (!line.isBlank()) {
                    if (taskCount == tasks.length) {
                        throw new IOException("The task file exceeds the task list capacity.");
                    }
                    tasks[taskCount] = parseSavedTask(line, isEncoded, isEscaped);
                    taskCount++;
                }
                line = reader.readLine();
            }
        }
        return taskCount;
    }

    /** Restores a task from the pipe-separated format used by the writer. */
    private static Task parseSavedTask(String line, boolean isEncoded, boolean isEscaped) throws IOException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw new IOException("Invalid saved task: " + line);
        }
        if (isEncoded) {
            try {
                for (int i = 2; i < fields.length; i++) {
                    fields[i] = StandardCharsets.UTF_8.newDecoder().decode(
                            java.nio.ByteBuffer.wrap(Base64.getDecoder().decode(fields[i]))).toString();
                }
            } catch (IllegalArgumentException exception) {
                throw new IOException("Invalid encoded task field.", exception);
            }
        }
        if (isEscaped) {
            for (int i = 2; i < fields.length; i++) {
                fields[i] = decodeField(fields[i]);
            }
        }
        if (fields[2].isBlank()) {
            throw new IOException("Saved task description is empty.");
        }
        Task task;
        if (fields[0].equals("T") && fields.length == 3) {
            task = new ToDo(fields[2]);
        } else if (fields[0].equals("D") && fields.length == 4) {
            task = new Deadline(fields[2], fields[3]);
        } else if (fields[0].equals("E") && fields.length == 5) {
            task = new Event(fields[2], fields[3], fields[4]);
        } else {
            throw new IOException("Invalid saved task: " + line);
        }
        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Restores escaped characters in the human-readable storage format. */
    private static String decodeField(String value) throws IOException {
        StringBuilder decoded = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (character != '\\') {
                decoded.append(character);
                continue;
            }
            if (++i == value.length()) {
                throw new IOException("Incomplete escape in saved task.");
            }
            switch (value.charAt(i)) {
            case '\\':
                decoded.append('\\');
                break;
            case 'p':
                decoded.append('|');
                break;
            case 'n':
                decoded.append('\n');
                break;
            case 'r':
                decoded.append('\r');
                break;
            default:
                throw new IOException("Invalid escape in saved task.");
            }
        }
        return decoded.toString();
    }

    /** Replaces the saved list after a successful task update, creating the data directory if needed. */
    private static void saveTasks(Task[] tasks, int taskCount) throws IOException {
        Files.createDirectories(TASK_FILE.getParent());
        Path temporary = Files.createTempFile(TASK_FILE.getParent(), "mybff-", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temporary)) {
                writer.write(STORAGE_HEADER);
                writer.newLine();
                for (int i = 0; i < taskCount; i++) {
                    writer.write(tasks[i].toStorageString());
                    writer.newLine();
                }
            }
            Files.move(temporary, TASK_FILE, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
