
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Login class.
 *
 * <p>
 * Description here.
 *
 * @author Luke Halpenny & Andrej Gorochov
 * @version 1.0
 */
public class Login {

    public static final String LOGIN_TEXT = Terminal.COLOR_YELLOW + "Enter master password (or 'q' to quit):\n" + Terminal.COLOR_RESET;
    public static final String LOGIN_PROMPT = ">> ";
    public static final String WARN_FNF = "Could not load user file. Create one now?\n";
    public static final String WARN_NO_USER_DATA = "Couldn't read user data. Create new user?\n\t(Note: Currently stored data will become unreadable)\n";
    public static final String ERR_NO_USER_DATA = "Couldn't read user data.\n";
    public static final String WARN_WEAK_PASSWORD = "Your password does not meet minimum requirements! Please use a stronger password.\n";
    public static final String ERR_EXIT = "Exiting...\n";
    public static final String ERR_WRITE = "Could not write to user file!\n";
    public static final String ERR_COMMON_FILE_MISSING = "Could not check against list of common passwords.\n";
    public static final String INCORRECT_PASSWORD = "Invalid password! Try again in %d seconds\n";
    public static final String FILE_PATH = "user.txt";

    private Terminal terminal;
    private int numAttempts;
    private String masterHash;
    private String masterSalt;
    private String decryptSalt;

    public Login(Terminal terminal) {
        this.terminal = terminal;
        this.numAttempts = 0;
        this.masterSalt = null;
        this.masterHash = null;
        try (Scanner inFile = new Scanner(new File(FILE_PATH))) {
            if (inFile.hasNextLine()) {
                this.masterSalt = inFile.nextLine();
            }
            if (inFile.hasNextLine()) {
                this.masterHash = inFile.nextLine();
            }
            if (inFile.hasNextLine()) {
                this.decryptSalt = inFile.nextLine();
            }
        } catch (FileNotFoundException e) {
            this.createNewUser();
        }
    }

    public String login() {
        if (this.masterHash == null || this.masterSalt == null || this.decryptSalt == null) {
            // Couldn't read user file
            this.terminal.warn(WARN_NO_USER_DATA);
            if (Utilities.getYesNoAnswer(this.terminal, "Yes/No >> ")) {
                this.createNewUser();
            } else {
                this.terminal.error(ERR_NO_USER_DATA);
                this.terminal.error(ERR_EXIT);
                System.exit(1);
            }
            return null;
        }
        boolean loggedIn = false;
        while (!loggedIn) {
            this.numAttempts++;
            this.terminal.info(LOGIN_TEXT);
            String masterPassword = this.terminal.readPassword(LOGIN_PROMPT);
            if (masterPassword.equalsIgnoreCase("q")) {
                return null;
            }
            try {
                Password pass = new Password(masterPassword, this.masterSalt);
                String key = pass.generateHash();
                if (key.equals(this.masterHash)) {
                    loggedIn = true;
                    pass = new Password(masterPassword, this.decryptSalt);
                    return pass.generateHash();
                } else {
                    this.loginWait();
                }
            } catch (IllegalArgumentException e) {
                this.loginWait();
            }
        }
        return null;
    }

    public void loginWait() {
        long waitSeconds = calcWaitSeconds();
        this.terminal.warn(String.format(INCORRECT_PASSWORD, waitSeconds));
        try {
            TimeUnit.SECONDS.sleep(waitSeconds);
        } catch (InterruptedException e) {
            terminal.error(e.getMessage());
        }
    }

    public long calcWaitSeconds() {
        return this.numAttempts > 0 ? (long) Math.pow(2, this.numAttempts - 1) : 0;
    }

    public void createNewUser() {
        this.masterSalt = Password.generateRandomSalt();
        this.terminal.info(Terminal.COLOR_YELLOW + "Please enter a master password." + Terminal.COLOR_RESET +
                " This will be used to encrypt all of your passwords.\n" +
                Terminal.COLOR_RED + "Please use a strong password that you have not used on any other sites.\n" +
                Terminal.COLOR_RESET +
                "Passwords have the following restrictions:\n" +
                Terminal.COLOR_BLUE + "\t- " + Terminal.COLOR_RESET + "Minimum 8 characters\n" +
                Terminal.COLOR_BLUE + "\t- " + Terminal.COLOR_RESET + "At least one uppercase letter\n" +
                Terminal.COLOR_BLUE + "\t- " + Terminal.COLOR_RESET + "At least one lowercase letter\n" +
                Terminal.COLOR_BLUE + "\t- " + Terminal.COLOR_RESET + "At least one number\n" +
                Terminal.COLOR_BLUE + "\t- " + Terminal.COLOR_RESET + "At least one symbol\n" +
                Terminal.COLOR_BLUE + "\t- " + Terminal.COLOR_RESET + "Not a commonly used password (123456, P@ssw0rd, etc.)\n");
        boolean needPassword = true;
        String masterPassword = null;
        while (needPassword) {
            masterPassword = terminal.readPassword(LOGIN_PROMPT);
            try {
                StoredPassword.checkPasswordStrength(masterPassword);
                needPassword = false;
            } catch (PasswordException e) {
                this.terminal.warn(WARN_WEAK_PASSWORD);
                this.terminal.warn(e.getMessage() + "\n");
            }
        }
        Password pass = new Password(masterPassword, this.masterSalt);
        this.masterHash = pass.generateHash();
        this.decryptSalt = Password.generateRandomSalt();
        this.writeToUserFile();
    }

    public void writeToUserFile() {
        try (
                FileWriter fileWriter = new FileWriter(FILE_PATH);
                PrintWriter printWriter = new PrintWriter(fileWriter)) {

            printWriter.println(this.masterSalt);
            printWriter.println(this.masterHash);
            printWriter.println(this.decryptSalt);
        } catch (IOException e) {
            this.terminal.error(ERR_WRITE);
            this.terminal.error(ERR_EXIT);
            System.exit(1);
        }

    }

    public static void writeToUserFile(Terminal terminal, String masterSalt, String masterHash, String decryptSalt) {
        try (
                FileWriter fileWriter = new FileWriter(FILE_PATH);
                PrintWriter printWriter = new PrintWriter(fileWriter)) {

            printWriter.println(masterSalt);
            printWriter.println(masterHash);
            printWriter.println(decryptSalt);
        } catch (IOException e) {
            terminal.error(ERR_WRITE);
            terminal.error(ERR_EXIT);
            System.exit(1);
        }

    }

}
