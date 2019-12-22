
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
 * @author Luke Halpenny & Andrej Gorochov
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
    private static final String ERR_LOAD_PASSWORDS = "Could not load passwords from file.";
    private static final String ERR_STORE_PASSWORDS = "Could not store passwords in file.";
    private static final String ERR_CMD = "Invalid command entered!";
    private static final String INPUT_PROMPT = "Enter >> ";
    private static final String HELP_TEXT =
            Terminal.COLOR_CYAN + "Super Amazing Password Manager" + Terminal.COLOR_RESET +
                    " is a tool to help you store all of your passwords for easy retrieval.\n" +
                    "Once you are logged in, you can navigate the menu by entering the key with the corresponding option.\n" +
                    "ALl navigating is text based, " +
                    Terminal.COLOR_RED + "the mouse is not used. " + Terminal.COLOR_RESET + "\n" +
                    "The program will tell you what to enter in order to proceed at each step.\n" +
                    "Thank you for using Super Amazing Password Manager!";
    private static final String MAIN_MENU_OPTIONS =
            Terminal.COLOR_BLUE + " 1. " + Terminal.COLOR_RESET + "Add new password\n" +
                    Terminal.COLOR_BLUE + " 2. " + Terminal.COLOR_RESET + "View passwords\n" +
                    Terminal.COLOR_BLUE + " 3. " + Terminal.COLOR_RESET + "Edit passwords\n" +
                    Terminal.COLOR_BLUE + " 4. " + Terminal.COLOR_RESET + "Remove passwords\n" +
                    Terminal.COLOR_BLUE + " 5. " + Terminal.COLOR_RESET + "Change master password\n" +
                    Terminal.COLOR_GREEN + " H. " + Terminal.COLOR_RESET + "Help\n" +
                    Terminal.COLOR_RED + " Q. " + Terminal.COLOR_RESET + "Quit";
    private static final String EDIT_PASSWORDS_MENU_OPTIONS =
            Terminal.COLOR_BLUE + " 1. " + Terminal.COLOR_RESET + "Edit password title\n" +
                    Terminal.COLOR_BLUE + " 2. " + Terminal.COLOR_RESET + "Edit password website\n" +
                    Terminal.COLOR_BLUE + " 3. " + Terminal.COLOR_RESET + "Edit password password\n" +
                    Terminal.COLOR_RED + " B. " + Terminal.COLOR_RESET + "Back";
    private static final String VIEW_PASSWORDS_MENU_OPTIONS =
            Terminal.COLOR_BLUE + " 1. " + Terminal.COLOR_RESET + "View all entries\n" +
                    Terminal.COLOR_BLUE + " 2. " + Terminal.COLOR_RESET + "Search by title\n" +
                    Terminal.COLOR_BLUE + " 3. " + Terminal.COLOR_RESET + "Search by website\n" +
                    Terminal.COLOR_RED + " B. " + Terminal.COLOR_RESET + "Back";
    private static final String GENERATE_PASSWORDS_OPTIONS =
            Terminal.COLOR_BLUE + " 1. " + Terminal.COLOR_RESET + "Generate easy to read password\n" +
                    Terminal.COLOR_BLUE + " 2. " + Terminal.COLOR_RESET + "Generate unrestricted password";
    private static final String DEFAULT_BORDER = "-";
    private static final int DEFAULT_BORDER_LENGTH = 80;
    private static final int MAX_PASSWORD_LENGTH = 2048;
    private static final String DEFAULT_USER_PASSWORDS_PATH = "passwordStore.txt";
    private static final String DEFAULT_USER_FILEPATH = "user.txt";
    public static final String MENU_TITLE = Terminal.COLOR_CYAN + "SUPER AMAZING PASSWORD MANAGER" + Terminal.COLOR_RESET;
    public static final String MENU_HELP_TITLE = Terminal.COLOR_GREEN + "HELP MENU" + Terminal.COLOR_RESET;
    public static final String MENU_GENPASS_TITLE = Terminal.COLOR_MAGENTA + "GENERATE PASSWORD" + Terminal.COLOR_RESET;
    public static final String MENU_VIEW_TITLE = Terminal.COLOR_CYAN + "VIEW PASSWORDS" + Terminal.COLOR_RESET;

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
            Utilities.printMenu(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH, MAIN_MENU_OPTIONS, MENU_TITLE);
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
                        displayEntries(this.passwords.getUserPasswords(), true);
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
                        Utilities.printMenu(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH, HELP_TEXT, MENU_HELP_TITLE);
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

    /**
     * Repeatedly asks for input from console until input meets the valid title
     * criteria of a StoredPassword.
     *
     * @return string of a valid title.
     */
    public String getValidTitleFromUser()
    {
        String title = "";
        boolean isValid = false;
        while (!isValid)
        {
            try
            {
                title = terminal.readLine("Enter title >> ");
                title = StoredPassword.validateTitle(title);
                isValid = true;
            }
            catch (IllegalArgumentException e)
            {
                terminal.error(e.getMessage() + " (Please try again)\n");
            }
        }
        return title;
    }

    /**
     * Repeatedly asks for input from console until input meets the valid
     * website criteria of a StoredPassword.
     *
     * @return string of a valid website.
     */
    public String getValidWebsiteFromUser()
    {
        String website = "";
        boolean isValid = false;
        while (!isValid)
        {
            try
            {
                website = terminal.readLine("Enter website >> ");
                website = StoredPassword.validateWebsite(website);
                isValid = true;
            }
            catch (IllegalArgumentException e)
            {
                terminal.error(e.getMessage() + " (Please try again)\n");
            }
        }
        return website;
    }

    /**
     * Repeatedly asks for input from console until input meets the valid
     * password criteria of a StoredPassword. [!WARNING] Method is now
     * depreciated, please use getValidPasswordFromUser(boolean).
     *
     * @return string of a valid password
     */
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
     * Repeatedly asks for input from console until input meets the valid title
     * criteria of a StoredPassword. Allows for enter to be pressed and a valid
     * password is generated if flag enabled.
     *
     * @param askToGeneratePassword flag for enabling pressing enter to generate
     * password.
     * @return string of a valid password.
     */
    private String getValidPasswordFromUser(boolean askToGeneratePassword)
    {
        String queryMessage = "Enter password ";
        if (askToGeneratePassword)
        {
            queryMessage += "(leave empty to generate password) ";
        }
        queryMessage += ">> ";
        String password = "";
        boolean isValid = false;
        while (!isValid)
        {
            try
            {
                password = terminal.readPassword(queryMessage);
                if (password.length() == 0 && askToGeneratePassword)
                {
                    password = generatePasswordMenu();
                    terminal.info(Terminal.COLOR_BLUE + "Generated password: " + Terminal.COLOR_RESET + password + "\n");
                }
                else
                {
                    StoredPassword.validatePassword(password);
                }
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
        String password = getValidPasswordFromUser(true);
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
        terminal.info("\n");
    }

//    private String generateValidPassword() {
//        String password;
//        if (Utilities.getYesNoAnswer(terminal, "Do you want us to generate a password for you? >>")) {
//
//            password = generatePasswordMenu();
//            terminal.info("Generated password: " + password + "\n");
//        } else {
//            password = getValidPasswordFromUser();
//        }
//        return password;
//    }
    /**
     * Generates a password according to the user choice.
     *
     * @return String of the generated password.
     */
    private String generatePasswordMenu()
    {
        String pass = "";
        boolean runMenu = true;
        while (runMenu)
        {
            Utilities.printMenu(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH, GENERATE_PASSWORDS_OPTIONS, MENU_GENPASS_TITLE);
            String command = terminal.readLine(INPUT_PROMPT);
            if (command.trim().length() == 1)
            {
                switch (command.trim().toUpperCase().charAt(0))
                {
                    case '1':
                    {
                        pass = StoredPassword.generateEasyToReadRandomPassword(Utilities.getInt(terminal, "Enter desired password length (min " + StoredPassword.MIN_PASSWORD_LENGTH + ")>> ", StoredPassword.MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
                        runMenu = false;
                        break;
                    }
                    case '2':
                    {
                        pass = StoredPassword.generateRandomPassword(Utilities.getInt(terminal, "Enter desired password length (min " + StoredPassword.MIN_PASSWORD_LENGTH + ")>> ", StoredPassword.MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
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
     * Options to view passwords.
     */
    private void viewPasswords()
    {
        boolean runMenu = true;
        while (runMenu)
        {
            Utilities.printMenu(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH, VIEW_PASSWORDS_MENU_OPTIONS, MENU_VIEW_TITLE);
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

    /**
     * Checks whether the password has been used before and gets user input for
     * if they want to use it again anyway.
     *
     * @param password password to be checked for.
     * @return true if the user wants to use the password again or if the
     * password is not being used in any other web. False if user doesn't want
     * to use the password again.
     */
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

    /**
     * Changes the master password of the user.
     */
    private void changeMasterPassword()
    {
        String oldMasterSalt = null;
        String oldMasterHash = null;
        try (Scanner inFile = new Scanner(new File(DEFAULT_USER_FILEPATH)))
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

    /**
     * Searches for entries by their title from user input. Entries are
     * displayed if found and empty if not found. Gives ability to reveal
     * passwords of found entries.
     */
    private void searchByTitle()
    {
        ArrayList<StoredPassword> searchPasswords = passwords.getUserPasswords(terminal.readLine("Enter title to search for >> "), new TitleSearchFilter());
        displayEntries(searchPasswords, true);
        if (searchPasswords.size() > 0 && Utilities.getYesNoAnswer(terminal, "Reveal passwords? >> "))
        {
            displayEntries(searchPasswords, false);
        }
    }

    /**
     * Searches for entries by their website from user input. Entries are
     * displayed if found and empty if not found. Gives ability to reveal
     * passwords of found entries.
     */
    private void searchByWebsite()
    {
        ArrayList<StoredPassword> searchPasswords = passwords.getUserPasswords(terminal.readLine("Enter website to search for >> "), new WebsiteSearchFilter());
        displayEntries(searchPasswords, true);
        if (searchPasswords.size() > 0 && Utilities.getYesNoAnswer(terminal, "Reveal passwords? >> "))
        {
            displayEntries(searchPasswords, false);
        }
    }

    /**
     * Displays all entries. Entries are displayed even if found or none are
     * found. Gives ability to reveal passwords of found entries.
     */
    private void viewAllEntries()
    {
        displayEntries(passwords.getUserPasswords(), true);
        if (passwords.getUserPasswords().size() > 0 && Utilities.getYesNoAnswer(terminal, "Reveal passwords? >> "))
        {
            displayEntries(passwords.getUserPasswords(), false);
        }
    }

    /**
     * Neatly displays the passed in entries with ability to display the entry
     * password.
     *
     * @param entries entries to be displayed
     * @param hidePassword flag for displaying password of entries. If true then
     * passwords won't be displayed, if false passwords will be visible.
     */
    private void displayEntries(List<StoredPassword> entries, boolean hidePassword)
    {
        Utilities.printString(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH);
        terminal.info("\n");
        terminal.info(String.format(Terminal.COLOR_BLUE + "%-7s   %-20s   %-30s   %s" + Terminal.COLOR_RESET + "\n",
                "ID", "Title", "Website", "Password"));
        Utilities.printString(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH);
        terminal.info("\n");
        for (StoredPassword entry : entries)
        {
            String password = "**********";
            if(passwords.isDuplicate(entry.getPassword())) {
                password = Terminal.COLOR_YELLOW + password + " [Duplicate]" + Terminal.COLOR_RESET;
            }
            if (!hidePassword)
            {
                password = entry.getPassword();
                if(passwords.isDuplicate(password)) {
                    password = Terminal.COLOR_YELLOW + password + " [Duplicate]" + Terminal.COLOR_RESET;
                }
            }
            terminal.info(String.format(Terminal.COLOR_MAGENTA + "%-7d" + Terminal.COLOR_RESET + " | %-20s | %-30s | %s\n",
                    entry.getId(), Utilities.cutAndAppendString(entry.getTitle(), 20 - 2, ".."),
                    Utilities.cutAndAppendString(entry.getWebsite(), 30 - 2, ".."), password));
        }
        Utilities.printString(terminal, DEFAULT_BORDER, DEFAULT_BORDER_LENGTH);
        terminal.info("\n");
    }

    /**
     * Options to edit passwords
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
                        displayEntries(this.passwords.getUserPasswords(), true);
                        editPasswordTitle();
                        break;
                    }
                    case '2':
                    {
                        displayEntries(this.passwords.getUserPasswords(), true);
                        editPasswordWebsite();
                        break;
                    }
                    case '3':
                    {
                        displayEntries(this.passwords.getUserPasswords(), true);
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

    /**
     * Displays the specified entries details and asks if the entry is the
     * correct entry. If no entry found displays error message. If entry not
     * confirmed displays message.
     *
     * @param passwordId entry to be found and confirmed.
     * @return true if user confirms entry. False if user doesn't confirm entry
     * or entry doesn't exist.
     */
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
            terminal.error("Password with such id doesn't exist\n");
        }
        return false;
    }

    /**
     * Displays details of a Stored Password by their id without displaying the
     * password.
     *
     * @param passwordId Stored Password id to be searched for
     * @return true if found and details displayed. False if not found.
     */
    private boolean displayPasswordDetails(int passwordId)
    {
        StoredPassword passwordDetails = passwords.getPasswordDetails(passwordId);
        if (passwordDetails != null)
        {
            terminal.info(Terminal.COLOR_BLUE + "Title        : " + Terminal.COLOR_RESET + passwordDetails.getTitle() +
                    Terminal.COLOR_BLUE + "\nWebsite      : " + Terminal.COLOR_RESET + passwordDetails.getWebsite() +
                    Terminal.COLOR_BLUE + "\nLast updated : " + Terminal.COLOR_RESET + passwordDetails.getLastUpdated()
                    + "\n");
            return true;
        }
        return false;
    }

    /**
     * Option to edit stored password title.
     */
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

    /**
     * Option to edit stored password website.
     */
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

    /**
     * Option to edit stored password password.
     */
    private void editPasswordPassword()
    {
        int passwordId = Utilities.getInt(terminal, "Enter stored password id >> ", 0, StoredPassword.getHighestTotalId());
        terminal.info("You are about to edit this entry:\n");
        if (confirmPassword(passwordId))
        {
            String password = getValidPasswordFromUser(true);
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

    /**
     * Writes all stored passwords in the current object's password storage into
     * passwords file.
     */
    private void savePasswords()
    {
        boolean done = passwords.readPasswordsOut(DEFAULT_USER_PASSWORDS_PATH, this.key);
        if (!done)
        {
            this.terminal.error(ERR_STORE_PASSWORDS);
        }
    }

    /**
     * Loads all stored passwords into the current object's password storage
     * from passwords file.
     */
    private void loadPasswords()
    {
        boolean done = passwords.readPasswordsIn(DEFAULT_USER_PASSWORDS_PATH, this.key);
        if (!done)
        {
            this.terminal.error(ERR_LOAD_PASSWORDS);
        }
    }

}
