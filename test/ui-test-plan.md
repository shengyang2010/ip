# UI Test Plan

Run with Java 25. Compare command responses exactly, including separators and blank lines. The unchanged startup banner and greeting are excluded from comparison; full actual sessions are recorded separately. Every session ends with bye and checks the farewell.

## Add a task

**Aim:** Accept and list a valid todo

**Inputs:**

```text
todo read book
list
bye
```

**Expected output (after greeting):**

```text
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
 Bye. Hope to see you again soon!
   ____________________________________________________________
```

## Empty todos and unknown commands

**Aim:** Reject incorrect inputs without adding tasks

**Inputs:**

```text
todo
todo   
blah

marking 1
unmark1
list
bye
```

**Expected output (after greeting):**

```text
    ____________________________________________________________
     OOPS!!! The description of a todo cannot be empty.
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! The description of a todo cannot be empty.
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! I'm sorry, but I don't know what that means :-(
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! I'm sorry, but I don't know what that means :-(
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! I'm sorry, but I don't know what that means :-(
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! I'm sorry, but I don't know what that means :-(
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
    ____________________________________________________________

    ____________________________________________________________
 Bye. Hope to see you again soon!
   ____________________________________________________________
```

## Valid tasks and task numbers

**Aim:** Continue after invalid numbers and preserve task behavior

**Inputs:**

```text
deadline homework /by Friday
event lunch /from noon /to evening
mark
mark abc
mark 0
mark 3
mark 9999999999999999
unmark -1
mark 1
unmark 1
list
bye
```

**Expected output (after greeting):**

```text
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] homework (by: Friday)
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] lunch (from: noon to: evening)
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [X] homework
    ____________________________________________________________

    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [ ] homework
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[D][ ] homework (by: Friday)
     2.[E][ ] lunch (from: noon to: evening)
    ____________________________________________________________

    ____________________________________________________________
 Bye. Hope to see you again soon!
   ____________________________________________________________
```

## Errors between additions and completion changes

**Aim:** Check counts, ordering and completion flags immediately after rejected inputs

**Inputs:**

```text
list
todo
todo first
blah
list
mark 1
todo
list
todo second
blah second
list
unmark 1

list
bye
```

**Expected output (after greeting):**

```text
    ____________________________________________________________
     Here are the tasks in your list:
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! The description of a todo cannot be empty.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] first
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! I'm sorry, but I don't know what that means :-(
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] first
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [X] first
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! The description of a todo cannot be empty.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] first
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] second
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! I'm sorry, but I don't know what that means :-(
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] first
     2.[T][ ] second
    ____________________________________________________________

    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [ ] first
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! I'm sorry, but I don't know what that means :-(
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] first
     2.[T][ ] second
    ____________________________________________________________

    ____________________________________________________________
 Bye. Hope to see you again soon!
   ____________________________________________________________
```

## Whitespace and command boundaries

**Aim:** Reject blank and misspelled commands while preserving descriptions and accepting whitespace around valid todos

**Inputs:**

```text
 	 
  todo	read book  
todo	  
list
todoish read
mark 1
TODO read
list
todo bye /by Friday
bye now
list
list extra
todo read book
list
bye
```

**Expected output (after greeting):**

```text
    ____________________________________________________________
     OOPS!!! I'm sorry, but I don't know what that means :-(
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! The description of a todo cannot be empty.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! I'm sorry, but I don't know what that means :-(
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [X] read book
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! I'm sorry, but I don't know what that means :-(
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] bye /by Friday
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! I'm sorry, but I don't know what that means :-(
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[T][ ] bye /by Friday
    ____________________________________________________________

    ____________________________________________________________
     OOPS!!! I'm sorry, but I don't know what that means :-(
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 3 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[T][ ] bye /by Friday
     3.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
 Bye. Hope to see you again soon!
   ____________________________________________________________
```

## Invalid indexes between valid status updates

**Aim:** Verify missing, noninteger, out-of-range and overflowing indexes cannot change existing task flags

**Inputs:**

```text
mark 1
todo first
unmark
todo second
mark 1 2
mark 2
unmark 0
list
mark 3
mark 1
unmark -2147483648
list
unmark 2147483648
unmark 2
mark 1.5
list
unmark abc
unmark 2
list
bye
```

**Expected output (after greeting):**

```text
    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] first
     Now you have 1 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] second
     Now you have 2 tasks in the list.
    ____________________________________________________________

    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [X] second
    ____________________________________________________________

    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] first
     2.[T][X] second
    ____________________________________________________________

    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     Nice! I've marked this task as done:
       [X] first
    ____________________________________________________________

    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] first
     2.[T][X] second
    ____________________________________________________________

    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [ ] second
    ____________________________________________________________

    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] first
     2.[T][ ] second
    ____________________________________________________________

    ____________________________________________________________
     Invalid task number.
    ____________________________________________________________

    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [ ] second
    ____________________________________________________________

    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] first
     2.[T][ ] second
    ____________________________________________________________

    ____________________________________________________________
 Bye. Hope to see you again soon!
   ____________________________________________________________
```

## Automatic file saving

**Aim:** Verify the file is created and replaced after each task change while the chatbot is still running.
Run `python test/test-storage.py` after the UI runner compiles the application. Each run uses an isolated
directory under `build/ui-test` to protect real task data.

**Inputs:** `todo read book`, `deadline homework /by Friday`, `event lunch /from noon /to evening`,
`mark 1`, `unmark 1`, `list`, `blah`, `bye` (one command at a time).

**Expected output:** Normal addition and completion confirmations, the three tasks in the list,
the existing unknown-command error, and the farewell. Full console input/output is recorded in
`test/storage-test-sessions.md`. Existing UI cases verify the exact response text.

**Expected file:** After each addition, `data/mybff.txt` contains all tasks added so far, one per line:

```text
T | 0 | read book
D | 0 | homework | Friday
E | 0 | lunch | noon | evening
```

Mark changes the first line to `T | 1 | read book`; unmark restores `T | 0 | read book`.
List, unknown commands, and bye leave the file unchanged. Startup alone creates no file.
New saves use a `MyBff storage v3` header and readable text fields with backslash escapes. The snapshots above
show decoded fields. Existing pipe-separated and Base64 v2 files remain readable.

## Storage errors and special text

**Aim:** Preserve existing data on failure and avoid crashes. Run `python test/test-storage-errors.py`.

**Inputs:** For each malformed file (invalid type/status/field count, empty description, bad UTF-8,
bad Base64, or more than 100 tasks), send `todo replacement`, `bye`.

**Expected output:** `OOPS!!! Could not load data/mybff.txt. Your saved file has not been changed.
Check the file and restart.` The application stops without changing the file.
A directory at the file path also gives this error. A blocked data directory either gives the load
error or rejects an addition with `OOPS!!! Could not save data/mybff.txt. No changes were made.
Check file access and try again.`

**Additional inputs:** Load a BOM-prefixed file with blank lines and a completed task; send `list`,
`todo pipes | and \ paths`, `bye`, then restart and list again. Expect both tasks with exact text and status.
Load 100 tasks and send `todo overflow`, `bye`: expect `Your task list is full.` and unchanged file bytes.
Full inputs and actual outputs are recorded in `test/storage-error-sessions.md`.
After loading two tasks, the error runner also blocks file replacement, then sends `mark 1`,
`unmark 2`, `todo rejected`, `list`, `bye`. Expect three save errors, no success confirmations,
the original two statuses and count, and no leftover temporary files.

## Load saved tasks on startup

**Aim:** Restore all task types and dates in order, then preserve loaded tasks when editing and adding.

**Inputs:** Restart after the saving case with `list`, `mark 2`, `todo next`, `bye`.
Restart again with `list`, `bye`.

**Expected output:** The first list shows the three saved tasks with their original dates and incomplete
statuses. Mark confirms homework is done. Adding next reports four tasks. The second restart lists:

```text
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][X] homework (by: Friday)
     3.[E][ ] lunch (from: noon to: evening)
     4.[T][ ] next
```

The runner checks exact responses, including separators and farewell, and records every session.

**Additional fixtures and inputs:** With `T | 1 | done`, `D | 0 | homework | `,
and `E | 1 | lunch |  | ` saved as separate lines, run `list`, `bye`.
Expect a completed todo, an incomplete deadline with an empty by field, and a completed event
with empty from/to fields. With an empty file, `list`, `bye` shows an empty list and leaves the file empty.
The original saving test also checks startup with no file.

## Readable storage migration

**Aim:** Read existing Base64 tasks and save readable text.

**Inputs:** With `MyBff storage v2` and `T | 1 | cmVhZCBib29r` as the saved file,
send `list`, `todo next`, `bye`.

**Expected output:** List shows `[T][X] read book`; addition confirms next and a count of two.
The file becomes `MyBff storage v3`, `T | 1 | read book`, `T | 0 | next` on separate lines.
Existing special-character restart tests verify pipes and backslashes survive the new format.

