package mybff;

/** Displays the current tasks without changing the list or its saved file. */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.printTaskList(tasks);
    }
}
