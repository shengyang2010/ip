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
                if (processCommand(command)) {
                    break;
                }
                ui.printResponseEnd();
            }
        } finally {
            ui.close();
        }
    }

    /** Routes each command to its operation and returns whether the application should stop. */
    private boolean processCommand(String command) {
        String commandWord = parser.getCommandWord(command);
        if (command.equals(Parser.COMMAND_BYE)) {
            Command exitCommand = new ExitCommand();
            exitCommand.execute(tasks, ui, storage);
            return exitCommand.isExit();
        } else if (command.equals(Parser.COMMAND_LIST)) {
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
        return false;
    }

    /** Parses a deletion, delegates execution, and reports malformed task numbers. */
    private void processDeleteCommand(String command) {
        try {
            int index = parser.parseTaskIndex(command);
            Command deleteCommand = new DeleteCommand(index);
            deleteCommand.execute(tasks, ui, storage);
        } catch (NumberFormatException exception) {
            ui.printInvalidTaskNumber();
        }
    }

    /** Parses an addition, delegates execution, and reports rejected commands. */
    private void processAddCommand(String command) {
        try {
            Task task = parser.createTask(command);
            Command addCommand = new AddCommand(task);
            addCommand.execute(tasks, ui, storage);
        } catch (MyBffException exception) {
            ui.printError(exception);
        }
    }

    /** Parses a completion change, delegates execution, and reports malformed task numbers. */
    private void processCompletionCommand(String command, boolean isMarkingAsDone) {
        try {
            int index = parser.parseTaskIndex(command);
            Command completionCommand = new CompletionCommand(index, isMarkingAsDone);
            completionCommand.execute(tasks, ui, storage);
        } catch (NumberFormatException exception) {
            ui.printInvalidTaskNumber();
        }
    }
}
