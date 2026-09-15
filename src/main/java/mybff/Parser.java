package mybff;

/** Interprets command words and task descriptions. */
public class Parser {
    public static final String COMMAND_DELETE = "delete";
    public static final String COMMAND_BYE = "bye";
    public static final String COMMAND_LIST = "list";
    public static final String COMMAND_MARK = "mark";
    public static final String COMMAND_UNMARK = "unmark";
    public static final String COMMAND_TODO = "todo";
    public static final String COMMAND_DEADLINE = "deadline";
    public static final String COMMAND_EVENT = "event";

    /** Returns the first word of a command. */
    public String getCommandWord(String command) {
        return command.split("\\s+", 2)[0];
    }

    /**
     * Creates a task from an add command.
     *
     * @throws MyBffException if the command is unknown or the todo description is empty
     */
    public Task createTask(String command) throws MyBffException {
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

}
