package mybff;

/** Represents a task in the task list. */
public class Task {
    private final String description;
    private boolean isDone;

    /** Creates an incomplete task with the given description. */
    public Task(String description) { 
        this.description = description; this.isDone = false; 
    }

    /** Returns the completion icon. */
    public String getStatusIcon() { 
        return isDone ? "X" : " "; 
    }

    /** Marks this task as done. */
    public void markAsDone() { 
        isDone = true; 
    }

    /** Marks this task as not done. */
    public void markAsNotDone() { 
        isDone = false; 
    }

    /** Returns the task description. */
    public String getDescription() { 
        return description; 
    }

    /** Returns the task type marker. */
    public String getTypeIcon() { 
        return "T"; 
    }

    /** Returns the display representation. */
    @Override 
    public String toString() { 
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description; 
    }
}
