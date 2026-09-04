package mybff;

/** Represents a task that must be completed by a date or time. */
public class Deadline extends Task {
    private final String by;

    /** Creates a deadline. */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String getTypeIcon() {
        return "D";
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
