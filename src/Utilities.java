
import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author
 */
public class Utilities
{

    public static final String COMMON_PASSWORDS_PATH = "common.txt";

    /**
     * Prints available menu options with a menu title bordered by the specified
     * string of a certain length.
     *
     * @param terminal terminal that prints the lines
     * @param borderString String combination of the menu border
     * @param borderLength amount of times the border string should be repeated
     * in the border
     * @param menuOptions the inside of the menu
     * @param menuTitle the title of the menu displayed before options
     */
    public static void printMenu(Terminal terminal, String borderString, int borderLength, String menuOptions, String menuTitle)
    {
        terminal.info("\n");
        printString(terminal, borderString, borderLength);
        terminal.info("\n" + menuTitle + "\n");
        printString(terminal, borderString, borderLength);
        terminal.info("\n" + menuOptions + "\n");
        printString(terminal, borderString, borderLength);
        terminal.info("\n");
    }

    /**
     * Prints a specified string the specified amount of times.
     *
     * @param terminal terminal that prints the lines
     * @param s string to be printed
     * @param amount amount of times for the string to be repeated.
     */
    public static void printString(Terminal terminal, String s, int amount)
    {
        for (int i = 0; i < amount; ++i)
        {
            terminal.info(s);
        }
    }

    /**
     * Queries user for integer input within specified range. Catches
     * InputMismatchException in case user enters a string for the integer and
     * continues querying until answer within range provided. If max value is
     * greater than min value it throws a RunTimeException.
     *
     * @param terminal terminal that prints the lines
     * @param query String query that will be asked of user.
     * @param min minimum value of range (included).
     * @param max maximum value of range (included).
     * @throws IllegalArgumentException if min is greater than max.
     * @return integer value from console within specified range.
     */
    public static int getInt(Terminal terminal, String query, int min, int max)
    {
        if (min > max)
        {
            throw new IllegalArgumentException("Min is greater than max.");
        }
        else
        {
            //set to min to please the IDE due to errors saying it might not 
            //be initiliazed even though there is no way it wouldn't (unless 
            //an error or other exceptions occur).
            int num = min;
            boolean isCorrect;
            do
            {
                try
                {
                    num = Integer.parseInt(terminal.readLine(query));
                    isCorrect = num >= min && num <= max;
                    if (!isCorrect)
                    {
                        terminal.error("Incorrect input. Try again. Input Range[" + min + "," + max + "]\n");
                    }
                }
                catch (InputMismatchException | NumberFormatException e)
                {
                    isCorrect = false;
                    terminal.error("Please use integer numbers.\n");
                }
            } while (!isCorrect);
            return num;
        }
    }

    /**
     * Gets either a yes or no answer from user from console.
     *
     * @param terminal terminal that prints out lines
     * @param query query prompt asking the user for input.
     * @return true if user answered yes, false if user answered no.
     */
    public static boolean getYesNoAnswer(Terminal terminal, String query)
    {
        String answer = terminal.readLine(query);
        while (answer.trim().length() < 1 || (answer.trim().toUpperCase().charAt(0) != 'Y' && answer.trim().toUpperCase().charAt(0) != 'N'))
        {
            terminal.error("Unknown answer. Please try again... (Answer can be either Y for yes or N for no)\n");
            answer = terminal.readLine(query);
        }
        return answer.trim().toUpperCase().charAt(0) == 'Y';
    }

    /**
     * Cuts the specified string at the cutting point and appends the other
     * specified string to the end of that new cut string. Throws an
     * IllegalArgumentException if string s is null. Throws an
     * IllegalArgumentException if appending string is null. Throws an
     * IllegalArgumentException if the cutting point is negative.
     *
     * @param s string to be cut.
     * @param cuttingPoint point at which string is to be cut.
     * @param appendedString string which is to be appended to the cut string
     * end.
     * @return the cut and appended string is returned.
     */
    public static String cutAndAppendString(String s, int cuttingPoint, String appendedString)
    {
        if (s == null)
        {
            throw new IllegalArgumentException("String s is null");
        }
        if (appendedString == null)
        {
            throw new IllegalArgumentException("String appendedString is null");
        }
        if (cuttingPoint < 0)
        {
            throw new IllegalArgumentException("Cutting point cannot be negative");
        }
        if (cuttingPoint >= s.length())
        {
            return s;
        }
        return s.substring(0, cuttingPoint) + appendedString;
    }

    /**
     * Tests whether a given string matches a regex.
     * @param str String to be tested
     * @param regex Regex pattern to test against
     * @return whether the given string matches the pattern
     */
    public static boolean matchesRegex(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    /**
     * Checks password against a list of common passwords
     * @param password Password to check
     * @return whether the password turns up on a list of common passwords
     */
    public static boolean isCommonPassword(String password) throws FileNotFoundException{
        File fileObj = new File(COMMON_PASSWORDS_PATH);
        try(Scanner scanner = new Scanner(fileObj)) {
            while(scanner.hasNextLine()) {
                String common = scanner.nextLine();
                if(password.equalsIgnoreCase(common)) {
                    return true;
                }
            }
            return false;
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Cannot find common password list");
        }
    }
}
