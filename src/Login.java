import java.io.File;
import java.io.FileNotFoundException;
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

    public static final double BACKOFF_BASE = 2;
    public static final String LOGIN_TEXT = "Enter master password (or 'q' to quit):\n";
    public static final String LOGIN_PROMPT = ">> ";
    public static final String ERR_FNF = "Could not load user file.\n";
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
    private double backoffBase;
    private long lastAttempt;
    private String masterHash;
    private String masterSalt;

    public Login(Terminal terminal) {
        this.terminal = terminal;
        this.numAttempts = 0;
        this.backoffBase = BACKOFF_BASE;
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
        } catch (FileNotFoundException e) {
            terminal.error(ERR_FNF);
        }
    }

    public boolean login() {
        if(this.masterHash == null) {
            return false;
        }
        boolean loggedIn = false;
        while(!loggedIn) {
            this.numAttempts++;
            terminal.info(LOGIN_TEXT);
            String masterPassword = terminal.readPassword(LOGIN_PROMPT);
            if(masterPassword.equalsIgnoreCase("q")) {
                return false;
            }
            Password pass = new Password(masterPassword, this.masterSalt);
            String key = pass.generateHash();
            masterPassword = null;
            if(key.equals(this.masterHash)) {
                return true;
            } else {
                this.lastAttempt = unixTime();
                this.loginWait();
            }
        }
        return true;
    }

    public void loginWait() {
        int waitSeconds = calcWaitSeconds();
        terminal.warn(String.format(INCORRECT_PASSWORD, waitSeconds));
        long nextAttempt = this.lastAttempt + waitSeconds;
        while(unixTime() < nextAttempt) {

        }
        return;
    }

    public int calcWaitSeconds() {
        return (int) Math.pow(2, this.numAttempts - 1);
    }


}
