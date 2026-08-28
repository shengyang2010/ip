import java.util.Scanner;

/**
 * Runs the MyBFF chatbot.
 */
public class MyBFF {
    private static final String SEPARATOR =
            "____________________________________________________________";

    /**
     * Greets the user, echoes commands, and exits when the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String banner =
                " __  ____   ______  ______ ______ \n"
                        + "|  \\/  \\ \\ / /  _ \\|  ____|  ____|\n"
                        + "| \\  / |\\ V /| |_) | |__  | |__   \n"
                        + "| |\\/| | | | |  _ <|  __| |  __|  \n"
                        + "| |  | | |.| | |_) | |    | |     \n"
                        + "|_|  |_| |_| |____/|_|    |_|     \n";

        System.out.println(SEPARATOR);
        System.out.println(banner);
        System.out.println("Hello! I'm MyBFF.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(SEPARATOR);

            if (command.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(SEPARATOR);
                break;
            }

            System.out.println(" " + command);
            System.out.println(SEPARATOR);
        }
        scanner.close();
    }
}
