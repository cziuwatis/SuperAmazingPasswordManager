import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

/**
 * Terminal class.
 *
 * <p>Uniform class for input/output through various methods.
 * Inspired by http://illegalargumentexception.blogspot.com/2010/09/java-systemconsole-ides-and-testing.html
 *
 * @author Luke Halpenny & Andrej Gorochov
 * @version 1.0
 */
public class Terminal {

    public static final String SETTINGS_FILE_PATH = "saps.properties";
    public static final String WARN_NO_CONFIG_FILE = "No settings file found.\n";

    public static final String COLOR_GRAY = "\u001B[38;5;8m";
    public static final String COLOR_RED = "\u001B[38;5;9m";
    public static final String COLOR_GREEN = "\u001B[38;5;10m";
    public static final String COLOR_YELLOW = "\u001B[38;5;11m";
    public static final String COLOR_BLUE = "\u001B[38;5;12m";
    public static final String COLOR_MAGENTA = "\u001B[38;5;13m";
    public static final String COLOR_CYAN = "\u001B[38;5;14m";
    public static final String COLOR_WHITE = "\u001B[38;5;15m";
    public static final String COLOR_RESET = "\u001B[0m";

    /**
     * Returns an ArrayList of ANSI-Color compatible terminals.
     * @return list of ANSI-Color compatible terminals.
     */
    private static ArrayList<String> getColorTerms() {
        ArrayList<String> colorTerms = new ArrayList<>();
        colorTerms.add("xterm-256color");
        colorTerms.add("xterm-color");
        colorTerms.add("putty");
        colorTerms.add("konsole");
        return colorTerms;
    }

    /**
     * Returns a String containing a sample color palette.
     * @return a String containing a sample color palette.
     */
    private static String sampleColors() {
        return COLOR_GRAY + "K" + COLOR_RED + "R" + COLOR_GREEN + "G" + COLOR_YELLOW + "Y" + COLOR_BLUE + "B" +
                COLOR_MAGENTA + "M" + COLOR_CYAN + "C" + COLOR_WHITE + "W" + COLOR_RESET;
    }

    private Scanner inputScanner;
    private PrintWriter outputWriter;
    private PrintWriter errorWriter;
    private Console console;
    private boolean supportsAnsi;
    private Properties settings;

    /**
     * Terminal is an abstraction class for dealing with text based terminals. It will test for color support as well
     * as allow for native password entering.
     */
    public Terminal() {
        // Set up input/output streams
        this.console = System.console();
        if (this.console != null) {
            this.inputScanner = new Scanner(this.console.reader());
            this.outputWriter = this.console.writer();
            this.errorWriter = this.console.writer();
        } else {
            this.inputScanner = new Scanner(System.in);
            this.outputWriter = new PrintWriter(System.out, true);
            // this.errorWriter = new PrintWriter(System.err, true); // Not always in sync with System.out, looks odd.
            this.errorWriter = new PrintWriter(System.out, true);
        }

        this.settings = new Properties();

        this.readSettingsFile();

        this.writeSettingsFile();

    }

    /**
     * Reads options such as color support from a settings file.
     */
    private void readSettingsFile() {
        // Read .ini file
        try (FileInputStream fileIn = new FileInputStream(SETTINGS_FILE_PATH)) {
            this.settings.load(fileIn);
            String color = settings.getProperty("enableColor", null);
            if (color == null) {
                boolean check = this.detectColor();
                if (check) {
                    this.supportsAnsi = true;
                    this.settings.setProperty("enableColor", "true");
                } else {
                    this.supportsAnsi = false;
                    this.settings.setProperty("enableColor", "false");
                }
            } else if (color.equalsIgnoreCase("true")) {
                this.supportsAnsi = true;
            } else if (color.equalsIgnoreCase("false")) {
                this.supportsAnsi = false;
            } else {
                // Invalid option
                boolean check = this.detectColor();
                if (check) {
                    this.supportsAnsi = true;
                    this.settings.setProperty("enableColor", "true");
                } else {
                    this.supportsAnsi = false;
                    this.settings.setProperty("enableColor", "false");
                }
            }
        } catch (FileNotFoundException e) {
            // Settings file not found
            this.warn(WARN_NO_CONFIG_FILE);
            boolean check = this.detectColor();
            if (check) {
                this.supportsAnsi = true;
                this.settings.setProperty("enableColor", "true");
            } else {
                this.supportsAnsi = false;
                this.settings.setProperty("enableColor", "false");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes settings to disc.
     */
    private void writeSettingsFile() {
        try (OutputStream output = new FileOutputStream(SETTINGS_FILE_PATH)) {
            this.settings.store(output, null);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Tries to detect whether the terminal supports color. Asks user if not detected.
     * @return whether to enable color support.
     */
    private boolean detectColor() {
        // Try to auto detect ANSI color support
        String termType = System.getenv().get("TERM");
        if (termType != null) {
            ArrayList<String> colorTerms = getColorTerms();
            if (colorTerms.contains(termType)) {
                return this.supportsAnsi = true;
            } else {
                return this.askColor();
            }
        } else {
            return this.askColor();
        }
    }

    /**
     * Asks the user whether to enable color support.
     * @return whether to enable color support.
     */
    private boolean askColor() {
        this.outputWriter.println("\t[!] Warning! Could not detect whether color is supported by this terminal.");
        this.outputWriter.printf("\t[!] Try use color anyway? (Color Sample: [%s]\n", sampleColors());
        this.outputWriter.print("Yes / No >> ");
        this.outputWriter.flush();
        while (this.inputScanner.hasNextLine()) {
            String ans = this.inputScanner.nextLine();
            if ("yes".equalsIgnoreCase(ans)) {
                this.supportsAnsi = true;
                return true;
            } else if ("no".equalsIgnoreCase(ans)) {
                this.supportsAnsi = false;
                return false;
            } else {
                this.outputWriter.println("\t[!] Invalid option.");
                this.outputWriter.println("\t[!] Try use color anyway?");
                this.outputWriter.print("Yes / No >> ");
                this.outputWriter.flush();
            }
        }
        return false;
    }

    /**
     * Returns a Scanner object for getting user input from the terminal.
     * @return a Scanner object.
     */
    public Scanner getInputScanner() {
        return this.inputScanner;
    }

    /**
     * Returns a Printwriter object connected to the terminals output.
     * @return a Printwriter object.
     */
    public PrintWriter getOutputWriter() {
        return this.outputWriter;
    }

    /**
     * Returns a Printwriter object connected to the terminals error output. May return the same as getOutputWriter().
     * @return a Printwriter object.
     */
    public PrintWriter getErrorWriter() {
        return this.errorWriter;
    }

    /**
     * Reads a user-entered password from the terminal.
     * @return a user-entered password from the terminal.
     */
    public String readPassword() {
        if (this.console != null) {
            return new String(this.console.readPassword());
        } else {
            return this.inputScanner.nextLine();
        }
    }

    /**
     * Reads a user-entered password from the terminal.
     * @param prompt prompt to print before getting input.
     * @return a user-entered password from the terminal.
     */
    public String readPassword(String prompt) {
        this.outputWriter.print(prompt);
        this.outputWriter.flush();
        if (this.console != null) {
            return new String(this.console.readPassword());
        } else {
            return this.inputScanner.nextLine();
        }
    }

    /**
     * Reads a user-entered String from the terminal.
     * @return a user-entered String from the terminal.
     */
    public String readLine() {
        if (this.console != null) {
            return this.console.readLine();
        } else {
            return this.inputScanner.nextLine();
        }
    }

    /**
     * Reads a user-entered String from the terminal.
     * @param prompt prompt to print before getting input.
     * @return a user-entered String from the terminal.
     */
    public String readLine(String prompt) {
        this.outputWriter.print(prompt);
        this.outputWriter.flush();
        if (this.console != null) {
            return this.console.readLine();
        } else {
            return this.inputScanner.nextLine();
        }
    }

    /**
     * Prints to the terminal.
     * @param text text to be printed.
     */
    public void info(String text) {
        if (!this.supportsAnsi) {
            text = text.replaceAll("\u001B\\[38;5;[0-9]+m", "");
        }
        this.outputWriter.print(text);
        this.outputWriter.flush();
    }

    /**
     * Prints a warning to the terminal. Will be prefixed with [Warning] and coloured yellow if color is supported.
     * @param text text to be printed.
     */
    public void warn(String text) {
        if (!this.supportsAnsi) {
            text = text.replaceAll("\u001B\\[38;5;[0-9]+m", "");
            text = "[Warning] " + text;
        } else {
            text = COLOR_YELLOW + "[Warning] " + text + COLOR_RESET;
        }
        this.outputWriter.print(text);
        this.outputWriter.flush();
    }

    /**
     * Prints an error to the terminal. Will be prefixed with [Error] and coloured red if color is supported.
     * @param text text to be printed.
     */
    public void error(String text) {
        if (!this.supportsAnsi) {
            text = text.replaceAll("\u001B\\[38;5;[0-9]+m", "");
            text = "[Error] " + text;
        } else {
            text = COLOR_RED + "[Error] " + text + COLOR_RESET;
        }
        this.errorWriter.print(text);
        this.errorWriter.flush();
    }

}
