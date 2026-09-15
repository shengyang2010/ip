package mybff;

/** Coordinates console interaction, command parsing, and the task list. */
public class MyBff {
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
            ui.printTaskDeleted(removedTask, tasks.size());
        } catch (NumberFormatException exception) {
            ui.printInvalidTaskNumber();
        }
    }

    /** Parses and adds a task, reporting rejected commands. */
    private void processAddCommand(String command) {
        try {
            Task task = parser.createTask(command);
            tasks.add(task);
            ui.printTaskAdded(task, tasks.size());
        } catch (MyBffException exception) {
            ui.printError(exception);
        }
    }

    /** Validates the task number and updates the selected task's status. */
    private void processCompletionCommand(String command, String commandWord, boolean isMarkingAsDone) {
        String taskNumber = command.substring(commandWord.length()).trim();
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (!tasks.isValidIndex(index)) {
                ui.printInvalidTaskNumber();
                return;
            }
            Task task = tasks.get(index);
            if (isMarkingAsDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            ui.printCompletionChanged(task, isMarkingAsDone);
        } catch (NumberFormatException exception) {
            ui.printInvalidTaskNumber();
        }
    }
}
