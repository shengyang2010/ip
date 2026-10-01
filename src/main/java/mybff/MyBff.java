package mybff;

import java.io.IOException;
import java.nio.file.Path;

/** Coordinates console interaction, command parsing, storage, and the task list. */
public class MyBff {
    private final Ui ui = new Ui();
    private final Parser parser = new Parser();
    private final Storage storage = new Storage(Path.of("data", "mybff.txt"));
    private TaskList tasks;

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
            tasks = storage.load();
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
            Command listCommand = new ListCommand();
            listCommand.execute(tasks, ui, storage);
        } else if (commandWord.equals(Parser.COMMAND_MARK)) {
            processCompletionCommand(command, true);
        } else if (commandWord.equals(Parser.COMMAND_UNMARK)) {
            processCompletionCommand(command, false);
        } else if (commandWord.equals(Parser.COMMAND_DELETE)) {
            processDeleteCommand(command);
        } else {
            processAddCommand(command);
        }
    }

    /** Validates the task number and removes the selected task. */
    private void processDeleteCommand(String command) {
        try {
            int index = parser.parseTaskIndex(command);
            if (!tasks.isValidIndex(index)) {
                ui.printInvalidTaskNumber();
                return;
            }
            Task removedTask = tasks.delete(index);
            try {
                storage.save(tasks);
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
                storage.save(tasks);
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
    private void processCompletionCommand(String command, boolean isMarkingAsDone) {
        try {
            int index = parser.parseTaskIndex(command);
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
                storage.save(tasks);
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
}
