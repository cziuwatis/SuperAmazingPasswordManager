import java.io.Console;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Terminal class.
 *
 * <p>Uniform class for input/output through various methods.
 * Inspired by http://illegalargumentexception.blogspot.com/2010/09/java-systemconsole-ides-and-testing.html
 *
 * @author Luke Halpenny
 * @version 1.0
 */
public class Terminal {

    private Scanner inputScanner;
    private PrintWriter outputWriter;
    private PrintWriter errorWriter;
    private Console console;
    private boolean supportsAnsi;
    public static final String COLOR_GRAY = "\u001B[38;5;8m";
    public static final String COLOR_RED = "\u001B[38;5;9m";
    public static final String COLOR_GREEN = "\u001B[38;5;10m";
    public static final String COLOR_YELLOW = "\u001B[38;5;11m";
    public static final String COLOR_BLUE = "\u001B[38;5;12m";
    public static final String COLOR_MAGENTA = "\u001B[38;5;13m";
    public static final String COLOR_CYAN = "\u001B[38;5;14m";
    public static final String COLOR_WHITE = "\u001B[38;5;15m";
    public static final String COLOR_RESET = "\u001B[0m";

    private static ArrayList<String> getColorTerms() {
        ArrayList<String> colorTerms = new ArrayList<>();
        colorTerms.add("xterm-256color");
        colorTerms.add("xterm-color");
        colorTerms.add("putty");
        colorTerms.add("konsole");
        return colorTerms;
    }

    private static String sampleColors() {
        return COLOR_GRAY + "K" + COLOR_RED + "R" + COLOR_GREEN + "G" + COLOR_YELLOW + "Y" + COLOR_BLUE + "B" +
                COLOR_MAGENTA + "M" + COLOR_CYAN + "C" + COLOR_WHITE + "W" + COLOR_RESET;
    }

    public Terminal() {
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
        String termType = System.getenv().get("TERM");
        if(termType != null) {
            ArrayList<String> colorTerms = getColorTerms();
            if(colorTerms.contains(termType)) {
                this.supportsAnsi = true;
            } else {
                this.askColor();
            }
        } else {
            this.askColor();
        }

    }

    private void askColor() {
        this.outputWriter.println("\t[!] Warning! Could not detect whether color is supported by this terminal.");
        this.outputWriter.printf("\t[!] Try use color anyway? (Color Sample: [%s]\n", sampleColors());
        this.outputWriter.print("Yes / No >> ");
        this.outputWriter.flush();
        while(this.inputScanner.hasNextLine()) {
            String ans = this.inputScanner.nextLine();
            if("yes".equalsIgnoreCase(ans)) {
                this.supportsAnsi = true;
                break;
            } else if("no".equalsIgnoreCase(ans)) {
                this.supportsAnsi = false;
                break;
            } else {
                this.outputWriter.println("\t[!] Invalid option.");
                this.outputWriter.println("\t[!] Try use color anyway?");
                this.outputWriter.print("Yes / No >> ");
            }
        }
    }

    public Scanner getInputScanner() {
        return this.inputScanner;
    }

    public PrintWriter getOutputWriter() {
        return this.outputWriter;
    }

    public PrintWriter getErrorWriter() {
        return this.errorWriter;
    }

    public String readPassword() {
        if(this.console != null) {
            return this.console.readPassword().toString();
        } else {
            return this.inputScanner.nextLine();
        }
    }

    public String readPassword(String prompt) {
        this.outputWriter.print(prompt);
        this.outputWriter.flush();
        if(this.console != null) {
            return new String(this.console.readPassword());
        } else {
            return this.inputScanner.nextLine();
        }
    }

    public String readLine() {
        if(this.console != null) {
            return this.console.readLine();
        } else {
            return this.inputScanner.nextLine();
        }
    }

    public String readLine(String prompt) {
        this.outputWriter.print(prompt);
        this.outputWriter.flush();
        if(this.console != null) {
            return this.console.readLine();
        } else {
            return this.inputScanner.nextLine();
        }
    }

    public void info(String text) {
        if(!this.supportsAnsi) {
            text.replaceAll("\u001B\\[38;5;[0-9]+m", "");
        }
        this.outputWriter.print(text);
    }

    public void warn(String text) {
        if(!this.supportsAnsi) {
            text.replaceAll("\u001B\\[38;5;[0-9]+m", "");
            text = "[Warning] " + text;
        } else {
            text = COLOR_YELLOW + "[Warning] " + text + COLOR_RESET;
        }
        this.outputWriter.print(text);
    }

    public void error(String text) {
        if(!this.supportsAnsi) {
            text.replaceAll("\u001B\\[38;5;[0-9]+m", "");
            text = "[Error] " + text;
        } else {
            text = COLOR_RED + "[Error] " + text + COLOR_RESET;
        }
        this.errorWriter.print(text);
    }

}
