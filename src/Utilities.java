
/**
 *
 * @author
 */
public class Utilities
{

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
}
