import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Menu class.
 *
 * <p>Handles all menu functions.</p>
 *
 * @author Luke Halpenny
 * @version 1.0
 */
public class Menu {

    /*
     *  CONSTANTS
     */
    private static final char HELP_CHAR = '3';
    private static final char QUIT_CHAR = '4';
    private static final String FILE_PATH = "ciphertext.txt";
    private static final String ERR_FNF = "\t[!] File not found!";
    private static final String ERR_CMD = "\t[!] Invalid command entered!";
    private static final String INPUT_PROMPT = ">> ";

    /*
     *  FIELDS
     */
    private boolean running;
    private Scanner input;
    private File fileObj;
    private String[] fileLines;

    /**
     * Constructor for Menu class.
     */
    public Menu() {
        this.input = new Scanner(System.in);
        this.fileObj = new File(FILE_PATH);
        try (Scanner fileIn = new Scanner(fileObj)) {
            this.fileLines = readLines(fileIn);
        } catch (FileNotFoundException e) {
            System.err.println(ERR_FNF);
            System.err.printf("\tCannot find file: %s\n", FILE_PATH);
            System.exit(0);
        }
    }

    /**
     * Main method, starts event loop of Menu. Blocks until exit command is entered.
     */
    public void run() {
        this.running = true;
        while (this.running) {
            printMenu();
            System.out.print(INPUT_PROMPT);
            String command = input.nextLine();
            if(command.length() == 1) {
                switch (command.charAt(0)) {
                    case '1': {
                        exercise1();
                        break;
                    }
                    case '2': {
                        exercise2();
                        break;
                    }
                    case HELP_CHAR: {
                        printHelp();
                        break;
                    }
                    case QUIT_CHAR: {
                        exit();
                        break;
                    }
                    default: {
                        System.err.println(ERR_CMD);
                        break;
                    }
                }
                System.out.println("Press any key to continue");
                input.nextLine();
            } else {
                System.err.println(ERR_CMD);
            }
        }
    }

    /**
     * Prints Menu text.
     */
    private void printMenu() {
        System.out.println();
        System.out.println("SHIFT CIPHER CA");
        System.out.println("-----------------------------");
        System.out.println("1. Decode caesar cipher");
        System.out.println("2. Crack shift cipher");
        System.out.println("3. Help");
        System.out.println("4. Quit");
    }

    /**
     * Prints help text for Menu.
     */
    private void printHelp() {
        System.out.println();
        System.out.println("Help:");
        System.out.println("Shift Cipher CA.");
        System.out.println("This program decrypts the file ciphertext.txt from the moodle.");
        System.out.println("There is a separate option for each question, as well as help and quit.");
        System.out.println("The class created can be used to encrypt, decypt or crack anything using shift cipher.");
        System.out.println("-----------------------------");
    }

    /**
     * Exits from main event loop.
     */
    private void exit() {
        System.out.println("Goodbye!");
        this.running = false;
    }

    /**
     * Runs question 1.
     */
    private void exercise1() {

    }

    /**
     * Runs question 2.
     */
    private void exercise2() {

    }

    /**
     * Reads in lines from a Scanner stream, returning them as a String[] object. Designed for reading text files.
     * @param input Scanner input to read from
     * @return String[] of lines from scanner.
     */
    private String[] readLines(Scanner input) {
        int count = 0;
        String fullRead = "";
        while(input.hasNextLine()) {
            fullRead += input.nextLine() + "\n";
            count++;
        }
        String[] out = new String[count];
        input.close();
        input = null;
        input = new Scanner(fullRead);
        for (int i = 0; i < count; i++) {
            out[i] = input.nextLine();
        }
        return out;
    }

}
