/**
 * Runs the MyBFF chatbot.
 */
public class MyBFF {
    private static final String SEPARATOR =
            "____________________________________________________________";

    /**
     * Greets the user and exits.
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
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(SEPARATOR);
    }
}
