
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
    private static final String GENERATE_PASSWORDS_OPTIONS = " 1. Generate easy to read password\n 2. Generate unrestricted password\n B. Back";
    private static final String DEFAULT_BORDER = "-";
    private static final String DEFAULT_USER_PASSWORDS_PATH = "passwordStore.txt";
    private static final String DEFAULT_USER_FILEPATH = "user.txt";
    private static final int DEFAULT_BORDER_LENGTH = 90;

    /*
     *  FIELDS
     */
    private boolean running;
    private PasswordStorage passwords;
    private final Terminal terminal;
    private String key;

    /**
     * Constructor for Menu class.
     */
    public Menu()
    {
        this.running = false;
        this.terminal = new Terminal();
        this.passwords = new PasswordStorage();
        this.key = ""; //key used to decrypt/encrypt password files
    }

    /**
     * Main method, starts event loop of Menu. Blocks until exit command is
     * entered.
     *
     * @param key key to be used for decryption/encryption of passwords
     */
    public void run(String key)
    {
        this.running = true;
        this.key = key;
        loadPasswords();
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
        savePasswords();
        terminal.info("Goodbye!");
        this.running = false;
    }

    public String getValidTitleFromUser()
    {
        String title = "";
        boolean isValid = false;
        while (!isValid)
        {
            try
            {
                title = terminal.readLine("Enter title >> ");
                StoredPassword.validateTitle(title);
                isValid = true;
            }
            catch (IllegalArgumentException e)
            {
                terminal.error(e.getMessage() + " (Please try again)\n");
            }
        }
        return title;
    }

    public String getValidWebsiteFromUser()
    {
        String website = "";
        boolean isValid = false;
        while (!isValid)
        {
            try
            {
                website = terminal.readLine("Enter website >> ");
                StoredPassword.validateWebsite(website);
                isValid = true;
            }
            catch (IllegalArgumentException e)
            {
                terminal.error(e.getMessage() + " (Please try again)\n");
            }
        }
        return website;
    }

    public String getValidPasswordFromUser()
    {
        String password = "";
        boolean isValid = false;
        while (!isValid)
        {
            try
            {
                password = terminal.readPassword("Enter password >> ");
                StoredPassword.validatePassword(password);
                isValid = true;
            }
            catch (PasswordException | IllegalArgumentException e)
            {
                terminal.error(e.getMessage() + " (Please try again)\n");
            }
        }
        return password;
    }

    /**
     * Adding a new password option.
     */
    private void addNewPassword()
    {
        Utilities.printString(terminal, "-", DEFAULT_BORDER_LENGTH);
        terminal.info("\nAdding a new password\n");
        Utilities.printString(terminal, "-", DEFAULT_BORDER_LENGTH);
        terminal.info("\n");
        String passwordTitle = getValidTitleFromUser();
        String passwordWebsite = getValidWebsiteFromUser();
        String password;
        if (Utilities.getYesNoAnswer(terminal, "Do you want us to generate a password for you? >> "))
        {

            password = generatePasswordMenu();
            terminal.info("Generated password: " + password + "\n");
        }
        else
        {
            password = getValidPasswordFromUser();
        }
        if (checkPasswordUsage(password))
        {
            passwords.addNewPassword(passwordTitle, passwordWebsite, password);
            savePasswords();
            displayPasswordDetails(passwords.getLatestPasswordId());
        }
        else
        {
            terminal.info("Password not added.\n");
        }
        password = null;
        terminal.info("\n");
    }

    private String generatePasswordMenu()
    {
        String pass = "";
        boolean runMenu = true;
        while (runMenu)
        {
            Utilities.printMenu(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH, GENERATE_PASSWORDS_OPTIONS, "GENERATE PASSWORDS MENU");
            String command = terminal.readLine(INPUT_PROMPT);
            if (command.trim().length() == 1)
            {
                switch (command.trim().toUpperCase().charAt(0))
                {
                    case '1':
                    {
                        pass = StoredPassword.generateEasyToReadRandomPassword(Utilities.getInt(terminal, "Enter desired password length (min 8)>> ", 8, Integer.MAX_VALUE));
                        runMenu = false;
                        break;
                    }
                    case '2':
                    {
                        pass = StoredPassword.generateRandomPassword(Utilities.getInt(terminal, "Enter desired password length (min 8)>> ", 8, Integer.MAX_VALUE));
                        runMenu = false;
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
        return pass;
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
            return Utilities.getYesNoAnswer(terminal, ">> ");
        }
        return true;
    }

    /**
     * Option to remove passwords.
     */
    private void removePasswords()
    {
        int passwordId = Utilities.getInt(terminal, "Enter stored password id >> ", 0, StoredPassword.getHighestTotalId());
        terminal.info("You are about to remove this entry:\n");
        if (confirmPassword(passwordId))
        {
            if (passwords.removePassword(passwordId))
            {
                savePasswords();
                terminal.info("Password entry has been removed\n");
            }
            //fail shouldn't be possible since confirming password found it above.
        }
    }

    private void changeMasterPassword()
    {
        String oldMasterSalt = null;
        String oldMasterHash = null;
        try (Scanner inFile = new Scanner(new File(DEFAULT_USER_FILEPATH));)
        {
            if (inFile.hasNextLine())
            {
                oldMasterSalt = inFile.nextLine();
            }
            if (inFile.hasNextLine())
            {
                oldMasterHash = inFile.nextLine();
            }
            if (oldMasterSalt != null && oldMasterHash != null)
            {
                terminal.warn("You are about to change your master password used to login into the service\n");
                terminal.info("Please enter your current master password to proceed.\n");
                if (new Password(getValidPasswordFromUser(), oldMasterSalt).matchesHash(oldMasterHash))
                {
                    String newMasterSalt = Password.generateRandomSalt();
                    String newDecryptSalt = Password.generateRandomSalt();
                    terminal.info("Master password matches! Now please enter a new master password.\n");
                    String newMasterPassword = getValidPasswordFromUser();
                    Login.writeToUserFile(terminal, newMasterSalt, new Password(newMasterPassword, newMasterSalt).generateHash(), newDecryptSalt);
                    this.key = new Password(newMasterPassword, newDecryptSalt).generateHash();
                    savePasswords();
                    terminal.info("Master password successfully changed!");
                }
                else
                {
                    terminal.error("Entered master password does not match!\n");
                }
            }
            else
            {
                terminal.error("Stored master salt and/or hash failed to load.");
            }
        }
        catch (FileNotFoundException e)
        {
            terminal.error("User text file was not found! Unable to change master password");
        }

    }

    private void searchByTitle()
    {
        ArrayList<StoredPassword> searchPasswords = passwords.getUserPasswords(terminal.readLine("Enter title to search for >> "), new TitleSearchFilter());
        displayEntries(searchPasswords, true);
        if (searchPasswords.size() > 0 && Utilities.getYesNoAnswer(terminal, "Reveal passwords? >> "))
        {
            displayEntries(searchPasswords, false);
        }
        searchPasswords = null;
    }

    private void searchByWebsite()
    {
        ArrayList<StoredPassword> searchPasswords = passwords.getUserPasswords(terminal.readLine("Enter website to search for >> "), new WebsiteSearchFilter());
        displayEntries(searchPasswords, true);
        if (searchPasswords.size() > 0 && Utilities.getYesNoAnswer(terminal, "Reveal passwords? >> "))
        {
            displayEntries(searchPasswords, false);
        }
        searchPasswords = null;
    }

    private void viewAllEntries()
    {
        displayEntries(passwords.getUserPasswords(), true);
        if (passwords.getUserPasswords().size() > 0 && Utilities.getYesNoAnswer(terminal, "Reveal passwords? >> "))
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
            if (Utilities.getYesNoAnswer(terminal, "Is this the correct entry? >> "))
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
        StoredPassword passwordDetails = passwords.getPasswordDetails(passwordId);
        if (passwordDetails != null)
        {
            terminal.info("Title        : " + passwordDetails.getTitle()
                    + "\nWebsite      : " + passwordDetails.getWebsite()
                    + "\nLast updated : " + passwordDetails.getLastUpdated()
                    + "\n");
            return true;
        }
        return false;
    }

    private void editPasswordTitle()
    {
        int passwordId = Utilities.getInt(terminal, "Enter stored password id >> ", 0, StoredPassword.getHighestTotalId());
        terminal.info("You are about to edit this entry:\n");
        if (confirmPassword(passwordId))
        {
            passwords.editPasswordTitle(passwordId, getValidTitleFromUser());
            savePasswords();
        }
    }

    private void editPasswordWebsite()
    {
        int passwordId = Utilities.getInt(terminal, "Enter stored password id >> ", 0, StoredPassword.getHighestTotalId());
        terminal.info("You are about to edit this entry:\n");
        if (confirmPassword(passwordId))
        {
            passwords.editPasswordWebsite(passwordId, getValidWebsiteFromUser());
            savePasswords();
        }
    }

    private void editPasswordPassword()
    {
        int passwordId = Utilities.getInt(terminal, "Enter stored password id >> ", 0, StoredPassword.getHighestTotalId());
        terminal.info("You are about to edit this entry:\n");
        if (confirmPassword(passwordId))
        {
            String password;
            if (Utilities.getYesNoAnswer(terminal, "Do you want us to generate a password for you? >>"))
            {

                password = generatePasswordMenu();
                terminal.info("Generated password: " + password + "\n");
            }
            else
            {
                password = getValidPasswordFromUser();
            }
            if (checkPasswordUsage(password))
            {
                passwords.editPasswordPassword(passwordId, password);
                savePasswords();
            }
            else
            {
                terminal.info("Password not added.\n");
            }
        }
    }

    private void savePasswords()
    {
        passwords.readPasswordsOut(DEFAULT_USER_PASSWORDS_PATH, this.key);
    }

    private void loadPasswords()
    {
        passwords.readPasswordsIn(DEFAULT_USER_PASSWORDS_PATH, this.key);
    }

}
