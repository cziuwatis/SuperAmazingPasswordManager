
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
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
    private static final char HELP_CHAR = 'H';
    private static final char QUIT_CHAR = 'Q';
    private static final char BACK_CHAR = 'B';
    private static final String ERR_CMD = "\t[!] Invalid command entered!";
    private static final String INPUT_PROMPT = "Enter >> ";
    private static final String HELP_TEXT = "";
    private static final String MAIN_MENU_OPTIONS = " 1. Add new password\n 2. View passwords\n 3. Edit passwords\n 4. Remove passwords\n 5. Change master password\n H. Help\n Q. Quit";
    private static final String EDIT_PASSWORDS_MENU_OPTIONS = " 1. Edit password title\n 2. Edit password website\n 3. Edit password password\n B. Back";
    private static final String VIEW_PASSWORDS_MENU_OPTIONS = " 1. View all entries\n 2. Search by title\n 3. Search by website\n B. Back";
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
            if (command.trim().length() == 1)
            {
                switch (command.trim().toUpperCase().charAt(0))
                {
                    case '1':
                    {
                        addNewPassword();
                        break;
                    }
                    case '2':
                    {
                        viewPasswords();
                        break;
                    }
                    case '3':
                    {
                        editPasswords();
                        break;
                    }
                    case '4':
                    {
                        removePasswords();
                        break;
                    }
                    case '5':
                    {
                        changeMasterPassword();
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
            passwords.addNewPassword("Unset", "Unset", "!2345678testpassword");
            int newPasswordId = passwords.getLatestPasswordId();
            passwords.editPasswordTitle(newPasswordId, terminal.readLine("\nEnter the title for the password >>"));
            passwords.editPasswordWebsite(newPasswordId, terminal.readLine("Enter the website for the password >>"));
            String tempPassword = terminal.readPassword("Enter the actual password >>");
            if (checkPasswordUsage(tempPassword))
            {
                passwords.editPasswordPassword(newPasswordId, tempPassword);
            }
            else
            {
                terminal.info("Password not added.\n");
            }
            tempPassword = null;
            displayPasswordDetails(newPasswordId);
            terminal.info("\n");
        }
        catch (IllegalArgumentException e)
        {
            terminal.error(e.getMessage());
            passwords.removeLatestPassword(); //remove latest added password
        }
    }

    /**
     * Option to view passwords
     */
    private void viewPasswords()
    {
        boolean runMenu = true;
        while (runMenu)
        {
            Utilities.printMenu(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH, VIEW_PASSWORDS_MENU_OPTIONS, "VIEW PASSWORDS MENU");
            String command = terminal.readLine(INPUT_PROMPT);
            if (command.trim().length() == 1)
            {
                switch (command.trim().toUpperCase().charAt(0))
                {
                    case '1':
                    {
                        viewAllEntries();
                        break;
                    }
                    case '2':
                    {
                        searchByTitle();
                        break;
                    }
                    case '3':
                    {
                        searchByWebsite();
                        break;
                    }
                    case BACK_CHAR:
                    {
                        runMenu = false;
                        break;
                    }
                    default:
                    {
                        terminal.error(ERR_CMD);
                        break;
                    }
                }
            }
            else
            {
                terminal.error(ERR_CMD);
            }
        }
    }

    private boolean checkPasswordUsage(String password)
    {

        if (passwords.isPasswordUsed(password))
        {
            terminal.warn("You have previously used this password, are you sure you want use it again?");
            return Utilities.getYesNoAnswer(terminal, ">>");
        }
        return true;
    }

    /**
     * Option to remove passwords.
     */
    private void removePasswords()
    {
        int passwordId = Utilities.getInt(terminal, "Enter stored password id >>", 0, StoredPassword.getHighestTotalId());
        terminal.info("You are about to remove this entry:\n");
        if (confirmPassword(passwordId))
        {
            if (passwords.removePassword(passwordId))
            {
                terminal.info("Password entry has been removed\n");
            }
            //fail shouldn't be possible since confirming password found it above.
        }
    }

    private void changeMasterPassword()
    {
        terminal.info("you just changed your master password, well done young one\n");
    }

    private void searchByTitle()
    {
        ArrayList<StoredPassword> searchPasswords = passwords.getUserPasswords(terminal.readLine("Enter title to search for >>"), new TitleSearchFilter());
        displayEntries(searchPasswords, true);
        if (searchPasswords.size() > 0 && Utilities.getYesNoAnswer(terminal, "Reveal passwords? >>"))
        {
            displayEntries(searchPasswords, false);
        }
        searchPasswords = null;
    }

    private void searchByWebsite()
    {
        ArrayList<StoredPassword> searchPasswords = passwords.getUserPasswords(terminal.readLine("Enter website to search for >>"), new WebsiteSearchFilter());
        displayEntries(searchPasswords, true);
        if (searchPasswords.size() > 0 && Utilities.getYesNoAnswer(terminal, "Reveal passwords? >>"))
        {
            displayEntries(searchPasswords, false);
        }
        searchPasswords = null;
    }

    private void viewAllEntries()
    {
        displayEntries(passwords.getUserPasswords(), true);
        if (passwords.getUserPasswords().size() > 0 && Utilities.getYesNoAnswer(terminal, "Reveal passwords? >>"))
        {
            displayEntries(passwords.getUserPasswords(), false);
        }
    }

    private void displayEntries(List<StoredPassword> entries, boolean hidePassword)
    {
        Utilities.printString(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH);
        terminal.info("\n");
        terminal.info(String.format("%-7s  %-20s  %-30s     %s\n", "ID", "   Title", "  Website", "Password"));
        Utilities.printString(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH);
        terminal.info("\n");
        for (StoredPassword entry : entries)
        {
            String password = "**********";
            if (!hidePassword)
            {
                password = entry.getPassword();
            }
            terminal.info(String.format("%-7d | %-20s | %-30s | %s\n", entry.getId(), Utilities.cutAndAppendString(entry.getTitle(), 20 - 2, ".."), Utilities.cutAndAppendString(entry.getWebsite(), 30 - 2, ".."), password));
            password = null; //to remove from memory?
        }
        Utilities.printString(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH);
        terminal.info("\n");
    }

    /**
     * Option to edit passwords
     */
    private void editPasswords()
    {
        boolean runMenu = true;
        while (runMenu)
        {
            Utilities.printMenu(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH, EDIT_PASSWORDS_MENU_OPTIONS, "EDIT PASSWORDS MENU");
            String command = terminal.readLine(INPUT_PROMPT);
            if (command.trim().length() == 1)
            {
                switch (command.trim().toUpperCase().charAt(0))
                {
                    case '1':
                    {
                        editPasswordTitle();
                        break;
                    }
                    case '2':
                    {
                        editPasswordWebsite();
                        break;
                    }
                    case '3':
                    {
                        editPasswordPassword();
                        break;
                    }
                    case BACK_CHAR:
                    {
                        runMenu = false;
                        break;
                    }
                    default:
                    {
                        terminal.error(ERR_CMD);
                        break;
                    }
                }
            }
            else
            {
                terminal.error(ERR_CMD);
            }
        }
    }

    private boolean confirmPassword(int passwordId)
    {
        if (displayPasswordDetails(passwordId))
        {
            if (Utilities.getYesNoAnswer(terminal, "Is this the correct entry? >>"))
            {
                return true;
            }
            else
            {
                terminal.info("Entry not confirmed\n");
            }
        }
        else
        {
            terminal.info("Password with such id doesn't exist\n");
        }
        return false;
    }

    private boolean displayPasswordDetails(int passwordId)
    {
        String[] passwordDetails = passwords.getPasswordDetails(passwordId);
        if (passwordDetails != null)
        {
            terminal.info("Title        : " + passwordDetails[0]
                    + "\nWebsite      : " + passwordDetails[1]
                    + "\nLast updated : " + passwordDetails[2]
                    + "\n");
            return true;
        }
        return false;
    }

    private void editPasswordTitle()
    {
        int passwordId = Utilities.getInt(terminal, "Enter stored password id >>", 0, StoredPassword.getHighestTotalId());
        terminal.info("You are about to edit this entry:\n");
        if (confirmPassword(passwordId))
        {
            //edit title
            try
            {
                passwords.editPasswordTitle(passwordId, terminal.readLine("Enter new title >>"));
            }
            catch (IllegalArgumentException e)
            {
                terminal.error(e.getMessage());
            }
        }
    }

    private void editPasswordWebsite()
    {
        int passwordId = Utilities.getInt(terminal, "Enter stored password id >>", 0, StoredPassword.getHighestTotalId());
        terminal.info("You are about to edit this entry:\n");
        if (confirmPassword(passwordId))
        {
            try
            {
                passwords.editPasswordWebsite(passwordId, terminal.readLine("Enter new website >>"));
            }
            catch (IllegalArgumentException e)
            {
                terminal.error(e.getMessage());
            }
        }
    }

    private void editPasswordPassword()
    {
        int passwordId = Utilities.getInt(terminal, "Enter stored password id >>", 0, StoredPassword.getHighestTotalId());
        terminal.info("You are about to edit this entry:\n");
        if (confirmPassword(passwordId))
        {
            try
            {
                String tempPassword = terminal.readLine("Enter new password >>");
                if (checkPasswordUsage(tempPassword))
                {
                    passwords.editPasswordPassword(passwordId, tempPassword);
                }
                else
                {
                    terminal.info("Password not added.\n");
                }
            }
            catch (IllegalArgumentException e)
            {
                terminal.error(e.getMessage());
            }
        }
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
