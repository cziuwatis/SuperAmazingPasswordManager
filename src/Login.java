import java.io.*;
import java.util.Scanner;

/**
 * Login class.
 *
 * <p>Description here.
 *
 * @author Luke Halpenny
 * @version 1.0
 */
public class Login {

    public static final String LOGIN_TEXT = "Enter master password (or 'q' to quit):\n";
    public static final String LOGIN_PROMPT = ">> ";
    public static final String WARN_FNF = "Could not load user file. Create one now?\n";
    public static final String WARN_NO_USER_DATA = "Couldn't read user data. Create new user?\n\t(Note: Currently stored data will become unreadable)\n";
    public static final String ERR_NO_USER_DATA = "Couldn't read user data.\n";
    public static final String WARN_WEAK_PASSWORD = "Your password does not meet minimum requirements! Please use a stronger password.\n";
    public static final String ERR_EXIT = "Exiting...";
    public static final String ERR_WRITE = "Could not write to user file!\n";
    public static final String ERR_NO_MASTER_PASSWORD = "No master password!\n";
    public static final String INCORRECT_PASSWORD = "Invalid password! Try again in %d seconds\n";
    public static final String FILE_PATH = "user.txt";

    /**
     * Returns current unix time in seconds. From exponential backoff sample on moodle.
     * @return current unix time.
     */
    public static long unixTime() {
        return System.currentTimeMillis() / 1000L;
    }

    private Terminal terminal;
    private int numAttempts;
    private long lastAttempt;
    private String masterHash;
    private String masterSalt;
    private String decryptSalt;

    public Login(Terminal terminal) {
        this.terminal = terminal;
        this.numAttempts = 0;
        this.lastAttempt = 0;
        this.masterSalt = null;
        this.masterHash = null;
        try(Scanner inFile = new Scanner(new File(FILE_PATH));) {
            if(inFile.hasNextLine()) {
                this.masterSalt = inFile.nextLine();
            }
            if(inFile.hasNextLine()) {
                this.masterHash = inFile.nextLine();
            }
            if(inFile.hasNextLine()) {
                this.decryptSalt = inFile.nextLine();
            }
        } catch (FileNotFoundException e) {
            this.terminal.warn(WARN_FNF);
            if(Utilities.getYesNoAnswer(this.terminal, "Yes/No >> ")) {
                this.createNewUser();
            } else {
                this.terminal.error(ERR_NO_USER_DATA);
                this.terminal.error(ERR_EXIT);
                System.exit(1);
            }
        }
    }

    public String login() {
        if(this.masterHash == null || this.masterSalt == null || this.decryptSalt == null) {
            // Couldnt read user file
            this.terminal.warn(WARN_NO_USER_DATA);
            if(Utilities.getYesNoAnswer(this.terminal, "Yes/No >> ")) {
                this.createNewUser();
            } else {
                this.terminal.error(ERR_NO_USER_DATA);
                this.terminal.error(ERR_EXIT);
                System.exit(1);
            }
            return null;
        }
        boolean loggedIn = false;
        while(!loggedIn) {
            this.numAttempts++;
            this.terminal.info(LOGIN_TEXT);
            String masterPassword = this.terminal.readPassword(LOGIN_PROMPT);
            if(masterPassword.equalsIgnoreCase("q")) {
                return null;
            }
            try {
                Password pass = new Password(masterPassword, this.masterSalt);
                String key = pass.generateHash();
                if(key.equals(this.masterHash)) {
                    pass = new Password(masterPassword, this.decryptSalt);
                    String decyptionKey = pass.generateHash();
                    return decyptionKey;
                } else {
                    this.lastAttempt = unixTime();
                    this.loginWait();
                }
            } catch (IllegalArgumentException e) {
                this.lastAttempt = unixTime();
                this.loginWait();
            }
        }
        return null;
    }

    public void loginWait() {
        long waitSeconds = calcWaitSeconds();
        this.terminal.warn(String.format(INCORRECT_PASSWORD, waitSeconds));
        long nextAttempt = this.lastAttempt + waitSeconds;
        while(unixTime() < nextAttempt) {

        }
        return;
    }

    public long calcWaitSeconds() {
        return this.numAttempts > 0 ? (long) Math.pow(2, this.numAttempts - 1) : 0;
    }

    public void createNewUser() {
        this.masterSalt = Password.generateRandomSalt();
        this.terminal.info("Please enter a master password. This will be used to encrypt all of your passwords.\n");
        this.terminal.info("Please use a strong password that you have not used on any other sites.\n");
        this.terminal.info("Passwords have the following restrictions:\n");
        this.terminal.info("\t- Minimum 8 characters\n\t- At least one uppercase letter\n" +
                "\t- At least one lowercase letter\n\t- At least one number\n\t- At least one symbol\n" +
                "\t- Not a commonly used password (123456, P@ssw0rd, etc.)\n");
        boolean needPassword = true;
        String masterPassword = null;
        while(needPassword) {
            masterPassword = terminal.readPassword(LOGIN_PROMPT);
            try {
                StoredPassword.checkPasswordStrength(masterPassword);
                needPassword = false;
            } catch (PasswordException e) {
                this.terminal.warn(WARN_WEAK_PASSWORD);
                this.terminal.warn(e.getMessage() + "\n");
            }
        }
        if(masterPassword == null) {
            this.terminal.error(ERR_NO_MASTER_PASSWORD);
            this.terminal.error(ERR_EXIT);
            System.exit(1);
        }
        Password pass = new Password(masterPassword, this.masterSalt);
        this.masterHash = pass.generateHash();
        this.decryptSalt = Password.generateRandomSalt();
        this.writeToUserFile();
    }

    public void writeToUserFile() {
        try(FileWriter fileWriter = new FileWriter(FILE_PATH);) {
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(this.masterSalt);
            printWriter.println(this.masterHash);
            printWriter.println(this.decryptSalt);
            printWriter.close();
        } catch (IOException e) {
            this.terminal.error(ERR_WRITE);
            this.terminal.error(ERR_EXIT);
            System.exit(1);
        }

    }


}
