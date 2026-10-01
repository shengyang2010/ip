package mybff;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

/** Coordinates console interaction, command parsing, storage, and the task list. */
public class MyBff {
    private static final int MAX_SAVE_ATTEMPTS = 5;
    private static final long SAVE_RETRY_DELAY_MILLIS = 100;
    private static final String STORAGE_HEADER = "MyBff storage v3";
    private static final Path TASK_FILE = Path.of("data", "mybff.txt");

    private final Ui ui = new Ui();
    private final Parser parser = new Parser();
    private final TaskList tasks = new TaskList();

    /**
     * Starts the chatbot.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new MyBff().run();
    }

    /** Greets the user and handles commands until bye or the end of input. */
    public void run() {
        try {
            loadTasks();
        } catch (IOException | SecurityException exception) {
            ui.printLoadError();
            ui.close();
            return;
        }

        ui.printGreeting();
        try {
            while (ui.hasNextCommand()) {
                String command = ui.readCommand();
                ui.printSeparator();
                if (command.equals(Parser.COMMAND_BYE)) {
                    ui.printFarewell();
                    break;
                }
                processCommand(command);
                ui.printResponseEnd();
            }
        } finally {
            ui.close();
        }
    }

    /** Routes each command to the appropriate task operation. */
    private void processCommand(String command) {
        String commandWord = parser.getCommandWord(command);
        if (command.equals(Parser.COMMAND_LIST)) {
            ui.printTaskList(tasks);
        } else if (commandWord.equals(Parser.COMMAND_MARK)) {
            processCompletionCommand(command, commandWord, true);
        } else if (commandWord.equals(Parser.COMMAND_UNMARK)) {
            processCompletionCommand(command, commandWord, false);
        } else if (commandWord.equals(Parser.COMMAND_DELETE)) {
            processDeleteCommand(command);
        } else {
            processAddCommand(command);
        }
    }

    /** Validates the task number and removes the selected task. */
    private void processDeleteCommand(String command) {
        String taskNumber = command.substring(Parser.COMMAND_DELETE.length()).trim();
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (!tasks.isValidIndex(index)) {
                ui.printInvalidTaskNumber();
                return;
            }
            Task removedTask = tasks.delete(index);
            try {
                saveTasks();
            } catch (IOException | SecurityException exception) {
                tasks.insert(index, removedTask);
                ui.printSaveError();
                return;
            }
            ui.printTaskDeleted(removedTask, tasks.size());
        } catch (NumberFormatException exception) {
            ui.printInvalidTaskNumber();
        }
    }

    /** Parses and adds a task, reporting rejected commands and save failures. */
    private void processAddCommand(String command) {
        try {
            Task task = parser.createTask(command);
            tasks.add(task);
            try {
                saveTasks();
            } catch (IOException | SecurityException exception) {
                tasks.delete(tasks.size() - 1);
                ui.printSaveError();
                return;
            }
            ui.printTaskAdded(task, tasks.size());
        } catch (MyBffException exception) {
            ui.printError(exception);
        }
    }

    /** Validates the task number and updates the selected task's status. */
    private void processCompletionCommand(String command, String commandWord,
            boolean isMarkingAsDone) {
        String taskNumber = command.substring(commandWord.length()).trim();
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (!tasks.isValidIndex(index)) {
                ui.printInvalidTaskNumber();
                return;
            }
            Task task = tasks.get(index);
            boolean wasDone = task.getStatusIcon().equals("X");
            if (isMarkingAsDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            try {
                saveTasks();
            } catch (IOException | SecurityException exception) {
                if (wasDone) {
                    task.markAsDone();
                } else {
                    task.markAsNotDone();
                }
                ui.printSaveError();
                return;
            }
            ui.printCompletionChanged(task, isMarkingAsDone);
        } catch (NumberFormatException exception) {
            ui.printInvalidTaskNumber();
        }
    }

    /** Loads saved tasks in order, or leaves the list empty on the first run. */
    private void loadTasks() throws IOException {
        if (Files.notExists(TASK_FILE)) {
            return;
        }
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
                    tasks.add(parseSavedTask(line, isEncoded, isEscaped));
                }
                line = reader.readLine();
            }
        }
    }

    /** Restores a task from the pipe-separated format used by the writer. */
    private Task parseSavedTask(String line, boolean isEncoded, boolean isEscaped)
            throws IOException {
        String[] fields = line.split(" \\| ", -1);
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw new IOException("Invalid saved task: " + line);
        }
        if (isEncoded) {
            try {
                for (int i = 2; i < fields.length; i++) {
                    fields[i] = new String(Base64.getDecoder().decode(fields[i]),
                            StandardCharsets.UTF_8);
                }
            } catch (IllegalArgumentException exception) {
                throw new IOException("Invalid encoded task field.", exception);
            }
        } else if (isEscaped) {
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

    /** Replaces the saved list after a successful task update. */
    private void saveTasks() throws IOException {
        Files.createDirectories(TASK_FILE.getParent());
        Path temporary = Files.createTempFile(TASK_FILE.getParent(), "mybff-", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temporary)) {
                writer.write(STORAGE_HEADER);
                writer.newLine();
                for (int i = 0; i < tasks.size(); i++) {
                    writer.write(tasks.get(i).toStorageString());
                    writer.newLine();
                }
            }
            replaceSavedFile(temporary);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    /** Retries briefly when access to the destination is temporarily denied. */
    private void replaceSavedFile(Path temporary) throws IOException {
        for (int attempt = 1; ; attempt++) {
            try {
                Files.move(temporary, TASK_FILE, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
                return;
            } catch (AccessDeniedException exception) {
                if (attempt == MAX_SAVE_ATTEMPTS) {
                    throw exception;
                }
                try {
                    Thread.sleep(SAVE_RETRY_DELAY_MILLIS);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted while waiting to save tasks.", interrupted);
                }
            }
        }
    }

}
