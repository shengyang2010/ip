# Each step declares its expected response independently of the chatbot.
$emptyTodo = '     OOPS!!! The description of a todo cannot be empty.'
$listHeader = '     Here are the tasks in your list:'
$invalidNumber = '     Invalid task number.'
$mixedCases = @(
    @{
        Name = 'Errors between additions and completion changes'
        Aim = 'Check counts, ordering and completion flags immediately after rejected inputs'
        Steps = @(
            @{ Input='list'; Output=@($listHeader) }
            @{ Input='todo'; Output=@($emptyTodo) }
            @{ Input='todo first'; Output=@("     Got it. I've added this task:", '       [T][ ] first', '     Now you have 1 tasks in the list.') }
            @{ Input='blah'; Output=@($unknown) }
            @{ Input='list'; Output=@($listHeader, '     1.[T][ ] first') }
            @{ Input='mark 1'; Output=@("     Nice! I've marked this task as done:", '       [X] first') }
            @{ Input='todo'; Output=@($emptyTodo) }
            @{ Input='list'; Output=@($listHeader, '     1.[T][X] first') }
            @{ Input='todo second'; Output=@("     Got it. I've added this task:", '       [T][ ] second', '     Now you have 2 tasks in the list.') }
            @{ Input='blah second'; Output=@($unknown) }
            @{ Input='list'; Output=@($listHeader, '     1.[T][X] first', '     2.[T][ ] second') }
            @{ Input='unmark 1'; Output=@("     OK, I've marked this task as not done yet:", '       [ ] first') }
            @{ Input=''; Output=@($unknown) }
            @{ Input='list'; Output=@($listHeader, '     1.[T][ ] first', '     2.[T][ ] second') }
        )
    },
    @{
        Name = 'Whitespace and command boundaries'
        Aim = 'Reject blank and misspelled commands while preserving descriptions and accepting whitespace around valid todos'
        Steps = @(
            @{ Input=" `t "; Output=@($unknown) }
            @{ Input="  todo`tread book  "; Output=@("     Got it. I've added this task:", '       [T][ ] read book', '     Now you have 1 tasks in the list.') }
            @{ Input="todo`t  "; Output=@($emptyTodo) }
            @{ Input='list'; Output=@($listHeader, '     1.[T][ ] read book') }
            @{ Input='todoish read'; Output=@($unknown) }
            @{ Input='mark 1'; Output=@("     Nice! I've marked this task as done:", '       [X] read book') }
            @{ Input='TODO read'; Output=@($unknown) }
            @{ Input='list'; Output=@($listHeader, '     1.[T][X] read book') }
            @{ Input='todo bye /by Friday'; Output=@("     Got it. I've added this task:", '       [T][ ] bye /by Friday', '     Now you have 2 tasks in the list.') }
            @{ Input='bye now'; Output=@($unknown) }
            @{ Input='list'; Output=@($listHeader, '     1.[T][X] read book', '     2.[T][ ] bye /by Friday') }
            @{ Input='list extra'; Output=@($unknown) }
            @{ Input='todo read book'; Output=@("     Got it. I've added this task:", '       [T][ ] read book', '     Now you have 3 tasks in the list.') }
            @{ Input='list'; Output=@($listHeader, '     1.[T][X] read book', '     2.[T][ ] bye /by Friday', '     3.[T][ ] read book') }
        )
    },
    @{
        Name = 'Invalid indexes between valid status updates'
        Aim = 'Verify missing, noninteger, out-of-range and overflowing indexes cannot change existing task flags'
        Steps = @(
            @{ Input='mark 1'; Output=@($invalidNumber) }
            @{ Input='todo first'; Output=@("     Got it. I've added this task:", '       [T][ ] first', '     Now you have 1 tasks in the list.') }
            @{ Input='unmark'; Output=@($invalidNumber) }
            @{ Input='todo second'; Output=@("     Got it. I've added this task:", '       [T][ ] second', '     Now you have 2 tasks in the list.') }
            @{ Input='mark 1 2'; Output=@($invalidNumber) }
            @{ Input='mark 2'; Output=@("     Nice! I've marked this task as done:", '       [X] second') }
            @{ Input='unmark 0'; Output=@($invalidNumber) }
            @{ Input='list'; Output=@($listHeader, '     1.[T][ ] first', '     2.[T][X] second') }
            @{ Input='mark 3'; Output=@($invalidNumber) }
            @{ Input='mark 1'; Output=@("     Nice! I've marked this task as done:", '       [X] first') }
            @{ Input='unmark -2147483648'; Output=@($invalidNumber) }
            @{ Input='list'; Output=@($listHeader, '     1.[T][X] first', '     2.[T][X] second') }
            @{ Input='unmark 2147483648'; Output=@($invalidNumber) }
            @{ Input='unmark 2'; Output=@("     OK, I've marked this task as not done yet:", '       [ ] second') }
            @{ Input='mark 1.5'; Output=@($invalidNumber) }
            @{ Input='list'; Output=@($listHeader, '     1.[T][X] first', '     2.[T][ ] second') }
            @{ Input='unmark abc'; Output=@($invalidNumber) }
            @{ Input='unmark 2'; Output=@("     OK, I've marked this task as not done yet:", '       [ ] second') }
            @{ Input='list'; Output=@($listHeader, '     1.[T][X] first', '     2.[T][ ] second') }
        )
    }
)
foreach ($case in $mixedCases) {
    $cases += @{
        Name = $case.Name
        Aim = $case.Aim
        Inputs = @($case.Steps | ForEach-Object { $_.Input })
        Lines = @($case.Steps | ForEach-Object { ,$_.Output })
    }
}
