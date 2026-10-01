package mybff;

/** Displays the farewell and signals that the application should stop. */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.printFarewell();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
