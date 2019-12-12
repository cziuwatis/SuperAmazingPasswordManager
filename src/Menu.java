
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Menu class.
 *
 * <p>
 * Handles all menu functions.</p>
 *
 * @author Luke Halpenny
 * @version 1.0
 */
public class Menu
{

    /*
     *  CONSTANTS
     */
    private static final char HELP_CHAR = '4';
    private static final char QUIT_CHAR = '5';
    private static final String ERR_CMD = "\t[!] Invalid command entered!";
    private static final String INPUT_PROMPT = "Enter >> ";
    private static final String HELP_TEXT = "";
    private static final String MAIN_MENU_OPTIONS = " 1. Add new password\n 2. View passwords\n 3. Edit passwords\n 4. Help\n 5. Quit";
    private static final String DEFAULT_BORDER = "-";
    private static final int DEFAULT_BORDER_LENGTH = 90;

    /*
     *  FIELDS
     */
    private boolean running;
    private PasswordStorage passwords;
    private final Terminal terminal;

    /**
     * Constructor for Menu class.
     */
    public Menu()
    {
        this.running = false;
        this.terminal = new Terminal();
        this.passwords = new PasswordStorage(); //@TODO read in passwords
    }

    /**
     * Main method, starts event loop of Menu. Blocks until exit command is
     * entered.
     */
    public void run()
    {
        this.running = true;
        while (this.running)
        {
            Utilities.printMenu(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH, MAIN_MENU_OPTIONS, "MAIN MENU");
            String command = terminal.readLine(INPUT_PROMPT);
            if (command.length() == 1)
            {
                switch (command.charAt(0))
                {
                    case '1':
                    {
                        addNewPassword();
                        break;
                    }
                    case '2':
                    {
                        exercise2();
                        break;
                    }
                    case '3':
                    {
                        exercise2();
                        break;
                    }
                    case HELP_CHAR:
                    {
                        Utilities.printMenu(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH, HELP_TEXT, "HELP TEXT");
                        break;
                    }
                    case QUIT_CHAR:
                    {
                        exit();
                        break;
                    }
                    default:
                    {
                        terminal.error(ERR_CMD);
                        break;
                    }
                }
                terminal.info("Press any key to continue\n");
            }
            else
            {
                terminal.error(ERR_CMD);
            }
        }
    }

    /**
     * Exits from main event loop.
     */
    private void exit()
    {
        terminal.info("Goodbye!");
        this.running = false;
    }

    /**
     * Adding a new password option.
     */
    private void addNewPassword()
    {
        Utilities.printString(terminal, "-", DEFAULT_BORDER_LENGTH);
        terminal.info("\nAdding a new password\n");
        Utilities.printString(terminal, "-", DEFAULT_BORDER_LENGTH);
        try
        {
            passwords.addNewPassword("Unset", "Unset", "Unset");
            int newPasswordId = passwords.getLatestPasswordId();
            passwords.editPasswordTitle(newPasswordId, terminal.readLine("Enter the title for the password >>"));
            passwords.editPasswordWebsite(newPasswordId, terminal.readLine("Enter the website for the password >>"));
            passwords.editPasswordPassword(newPasswordId, terminal.readPassword("Enter the actual password >>"));
            terminal.info("Congratz mate, ya password is safe with us yarr.");
        }
        catch (IllegalArgumentException e)
        {
            terminal.error(e.getMessage());
            passwords.removeLatestPassword(); //remove latest added password
        }
    }

    /**
     * Runs question 2.
     */
    private void exercise2()
    {

    }

    /**
     * Reads in lines from a Scanner stream, returning them as a String[]
     * object. Designed for reading text files.
     *
     * @param input Scanner input to read from
     * @return String[] of lines from scanner.
     */
    private String[] readLines(Scanner input)
    {
        int count = 0;
        String fullRead = "";
        while (input.hasNextLine())
        {
            fullRead += input.nextLine() + "\n";
            count++;
        }
        String[] out = new String[count];
        input.close();
        input = null;
        input = new Scanner(fullRead);
        for (int i = 0; i < count; i++)
        {
            out[i] = input.nextLine();
        }
        return out;
    }

}
