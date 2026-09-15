
## Automatic file saving

**Aim:** Verify the file is created and replaced after each task change while the chatbot is still running.
Run `python test/test-storage.py` after the UI runner compiles the application. Each run uses an isolated
directory under `build/ui-test` to protect real task data.

**Inputs:** `todo read book`, `deadline homework /by Friday`, `event lunch /from noon /to evening`,
`mark 1`, `unmark 1`, `list`, `blah`, `bye` (one command at a time).

**Expected output:** Normal addition and completion confirmations, the three tasks in the list,
the existing unknown-command error, and the farewell. Full console input/output is recorded in
`test/storage-test-sessions.md`. Existing UI cases verify the exact response text.

**Expected file:** After each addition, `data/duke.txt` contains all tasks added so far, one per line:

```text
T | 0 | read book
D | 0 | homework | Friday
E | 0 | lunch | noon | evening
```

Mark changes the first line to `T | 1 | read book`; unmark restores `T | 0 | read book`.
List, unknown commands, and bye leave the file unchanged. Startup alone creates no file.
New saves use a `MyBff storage v2` header and Base64-encoded text fields. The snapshots above
show decoded fields. Existing pipe-separated files remain readable.

## Storage errors and special text

**Aim:** Preserve existing data on failure and avoid crashes. Run `python test/test-storage-errors.py`.

**Inputs:** For each malformed file (invalid type/status/field count, empty description, bad UTF-8,
bad Base64, or more than 100 tasks), send `todo replacement`, `bye`.

**Expected output:** `OOPS!!! Could not load data/duke.txt. Your saved file has not been changed.
Check the file and restart.` The application stops without changing the file.
A directory at the file path also gives this error. A blocked data directory either gives the load
error or rejects an addition with `OOPS!!! Could not save data/duke.txt. No changes were made.
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
