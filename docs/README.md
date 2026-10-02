# MyBff User Guide

MyBff is a terminal chatbot that keeps track of your to-dos, deadlines, and events. Type a command, press **Enter**, and let MyBff keep your task list organised.

## Quick start

1. Install **Java 25**. Check your version with `java -version`.
2. Place `mybff.jar` in a folder of your choice. If you need to build it first, follow the [build instructions](../README.md#building-and-running-a-fat-jar).
3. Open a terminal in that folder and run:

   ```text
   java -jar mybff.jar
   ```

4. Try `todo read book`, then `list`. Type `bye` when you are finished.

## Features

### Command basics

- Enter one command per line, using lowercase command words.
- Replace uppercase placeholders such as `DESCRIPTION` with your own text; do not type the placeholders themselves.
- Keep the order and spaces around `/by`, `/from`, and `/to` shown in the examples.
- Dates and times are stored as text, so you can use values such as `Friday` or `2 Oct 2026 18:00`. MyBff does not validate dates or send reminders.
- For `mark`, `unmark`, and `delete`, use a positive task number from the latest **`list`** output.

### Add a to-do: `todo`

Adds a task without a date or time.

**Format:** `todo DESCRIPTION`

**Example:** `todo read book`

MyBff confirms the new task and the total number of tasks. The task appears as `[T][ ] read book`.

### Add a deadline: `deadline`

Adds a task with a due date or time.

**Format:** `deadline DESCRIPTION /by WHEN`

**Example:** `deadline submit report /by Friday 18:00`

The task appears as `[D][ ] submit report (by: Friday 18:00)`.

### Add an event: `event`

Adds a task with a start and end time.

**Format:** `event DESCRIPTION /from START /to END`

**Example:** `event team meeting /from Monday 14:00 /to Monday 15:00`

The task appears as `[E][ ] team meeting (from: Monday 14:00 to: Monday 15:00)`.

### View all tasks: `list`

Shows all tasks, including completed ones, in the order you added them.

**Format and example:** `list`

After adding the three examples above, the list looks like this:

```text
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] submit report (by: Friday 18:00)
3.[E][ ] team meeting (from: Monday 14:00 to: Monday 15:00)
```

`[T]` means to-do, `[D]` deadline, and `[E]` event. `[ ]` means not done; `[X]` means done. An empty list shows only the heading.

### Find tasks: `find`

Finds tasks whose **descriptions** contain your search text. Dates and times are not searched.

**Format:** `find KEYWORD`

**Example:** `find book` matches `read book` and `buy books`, but not `read Book`: searches are **case-sensitive**. Multiple words are matched as one phrase, so `find read book` looks for that exact phrase.

If nothing matches, MyBff shows the matching-tasks heading with no tasks below it.

> Search results are numbered from 1 separately. Run `list` before marking or deleting a task, and use its number from the full list.

### Mark a task as done or not done: `mark` / `unmark`

Changes a task's completion status without removing it.

**Formats:** `mark NUMBER` and `unmark NUMBER`

**Examples:** `mark 1` marks the first task in the full list as done (`[X]`). `unmark 1` changes it back to not done (`[ ]`). MyBff confirms the updated status and task description.

### Delete a task: `delete`

Removes a task and confirms the remaining task count.

**Format:** `delete NUMBER`

**Example:** `delete 2` removes the second task in the full list.

Deletion is immediate and has no undo command. Remaining tasks are renumbered, so run `list` again before your next change.

### Exit: `bye`

**Format and example:** `bye`

MyBff says goodbye and closes.

### Save your tasks automatically

MyBff saves every successful addition, deletion, or status change to `data/mybff.txt` and reloads your tasks on startup. There is no save command.

The `data` folder is relative to the folder your terminal runs MyBff from. Always start from the same folder to use the same task list. To back up or transfer your tasks, close MyBff and copy `data/mybff.txt` along with the JAR, keeping the same folder structure.

## If something goes wrong

- **Unknown command:** Check spelling and lowercase letters. Enter `list` and `bye` on their own.
- **Invalid task number:** Run `list` and choose an existing number, starting at 1.
- **Empty to-do or search:** Add a description after `todo` or search text after `find`.
- **Could not save:** The attempted change was cancelled. Check that the data folder is writable and the file is not locked, then retry the command.
- **Could not load:** MyBff stops and leaves your saved file unchanged. Check that the file is readable, or restore a known-good backup, then restart.
