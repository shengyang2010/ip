package mybff;

import java.util.ArrayList;

/** Stores tasks in insertion order using a dynamically sized collection. */
public class TaskList {
    private final ArrayList<Task> tasks = new ArrayList<>();

    public int size() {
        return tasks.size();
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    /** Reports whether the zero-based index identifies a stored task. */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Removes and returns the task at a zero-based index, preserving the remaining order.
     *
     * @throws IndexOutOfBoundsException if the index does not identify a stored task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /** Appends a task to the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Inserts a task at a zero-based index, for restoring a failed deletion. */
    public void insert(int index, Task task) {
        tasks.add(index, task);
    }
}
