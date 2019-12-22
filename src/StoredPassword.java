
import java.security.SecureRandom;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * StoredPassword class.
 *
 * <p>
 * Stores details about password's entry like id, title, website and password.
 * </p>
 *
 * @author Luke Halpenny & Andrej Gorochov
 * @version 1.0
 */
public final class StoredPassword
{

    public final static int MIN_PASSWORD_LENGTH = 8;
    private static int totalIds = 0;
    private int id;
    private String title;
    private String website;
    private String password;
    private LocalDateTime lastUpdated;

    /**
     * Generates a random password of specified length in the ASCII character 
     * set between 94 and 126.
     * @param length length of password to generate
     * @return String of the generated password.
     */
    public static String generateRandomPassword(int length)
    {
        SecureRandom rand = new SecureRandom();
        StringBuilder pass = new StringBuilder();
        if (length < MIN_PASSWORD_LENGTH)
        {
            throw new IllegalArgumentException("Password length needs to be at least " + MIN_PASSWORD_LENGTH);
        }
        boolean isValidPassword = false;
        while (!isValidPassword)
        {
            pass = new StringBuilder();
            try
            {
                for (int i = 0; i < length; i++)
                {
                    pass.append((char) (rand.nextInt(94) + 32));//32 start of ASCII characters, 94 difference between end of ASCI and start
                }
                StoredPassword.validatePassword(pass.toString());
                isValidPassword = true;
            }
            catch (PasswordException ignored)
            {
            }
        }
        return pass.toString();
    }

    /**
     * Generates an easy to say password. Easy to say only includes letters 
     * uppercase/lowercase from the English alphabet, no symbols or numbers.
     * @param length length of password to be generated
     * @return String of the generated password
     */
    public static String generateEasyToSayRandomPassword(int length)
    {
        SecureRandom rand = new SecureRandom();
        StringBuilder pass = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            char passChar = (char) (rand.nextInt(26) + 65);//26 for alphabet size, 65 for start of ASCII characters
            if (rand.nextInt(100) < 50) //50/50 chance either capital or lowercase
            {
                passChar = Character.toLowerCase(passChar);
            }
            pass.append(passChar);
        }
        return pass.toString();
    }

    /**
     * Generates an easy to read password. Easy to read excludes characters 
     * that may confuse someone when reading the password i.e l (letter L) 
     * and 1 (number 1) are excluded, o O 0 are excluded etc.
     * @param length length of password to generate
     * @return String of the generated password
     */
    public static String generateEasyToReadRandomPassword(int length)
    {
        SecureRandom rand = new SecureRandom();
        StringBuilder pass = new StringBuilder();
        if (length < MIN_PASSWORD_LENGTH)
        {
            throw new IllegalArgumentException("Password length needs to be at least " + MIN_PASSWORD_LENGTH);
        }
        boolean isValidPassword = false;
        while (!isValidPassword)
        {
            pass = new StringBuilder();
            try
            {
                for (int i = 0; i < length; i++)
                {
                    char passChar = (char) (rand.nextInt(94) + 32);//94 is size of character set, 32 is start of ASCII characters used to generate password
                    if (passChar == 'i' || passChar == 'L' || passChar == 'I'
                            || passChar == 'B' || passChar == '8'
                            || passChar == 'l' || passChar == '1' || passChar == '|'
                            || passChar == 'o' || passChar == 'O' || passChar == '0'
                            || passChar == ' ' || passChar == ':' || passChar == ';')
                    {
                        i--;
                    }
                    else
                    {
                        pass.append(passChar);
                    }
                }
                StoredPassword.validatePassword(pass.toString());
                isValidPassword = true;
            }
            catch (PasswordException ignored)
            {
            }
        }
        return pass.toString();
    }

    /**
     * Checks the password strength. Checks whether password contains at least 
     * one number, symbol, capital and lower case letter. It as well needs to 
     * be at least a certain amount of characters and not match any of the
     * passwords from the password common list text file. Throws 
     * PasswordException if doesn't pass any of the criteria.
     * @param password password to strength check
     * @return true if all checks passed
     */
    public static boolean checkPasswordStrength(String password)
    {
        try
        {
            if (Utilities.isCommonPassword(password))
            {
                throw new PasswordException("Password must not match a common password.");
            }
        }
        catch (FileNotFoundException ignored)
        {
        }
        if (password.length() < MIN_PASSWORD_LENGTH)
        {
            throw new PasswordException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
        String hasNumber = "[0-9]+";
        if (!Utilities.matchesRegex(password, hasNumber))
        {
            throw new PasswordException("Password must have at least one number.");
        }
        String hasLower = "[a-z]+";
        if (!Utilities.matchesRegex(password, hasLower))
        {
            throw new PasswordException("Password must have at least one lowercase letter.");
        }
        String hasUpper = "[A-Z]+";
        if (!Utilities.matchesRegex(password, hasUpper))
        {
            throw new PasswordException("Password must have at least one uppercase letter.");
        }
        String hasSymbol = "[^a-zA-z0-9]+";
        if (!Utilities.matchesRegex(password, hasSymbol))
        {
            throw new PasswordException("Password must have at least one symbol.");
        }
        return true;
    }

    /**
     * Gets the highest available id of StoredPassword instances.
     * @return id of highest available StoredPassword id.
     */
    public static int getHighestTotalId()
    {
        return totalIds;
    }

    /**
     * StoredPassword constructor with all fields.
     * @param id id of the StoredPassword created.
     * @param title title of the StoredPassword created.
     * @param website website of the StoredPassword created.
     * @param password password of the StoredPassword created.
     * @param lastUpdated lastUpdatedDate.
     */
    public StoredPassword(int id, String title, String website, String password, LocalDateTime lastUpdated)
    {
        this.setId(id);
        this.setTitle(title);
        this.setWebsite(website);
        this.setPassword(password);
        this.setLastUpdated(lastUpdated);
    }

    /**
     * StoredPassword constructor with all fields except lastUpdated.
     * @param id id of the StoredPassword created.
     * @param title title of the StoredPassword created.
     * @param website website of the StoredPassword created.
     * @param password password of the StoredPassword created.
     */
    public StoredPassword(int id, String title, String website, String password)
    {
        this.setId(id);
        this.setTitle(title);
        this.setWebsite(website);
        this.setPassword(password);
        this.setLastUpdated();
    }

    /**
     * StoredPassword constructor for automatic id generation, without 
     * lastUpdated and id.
     * @param title title of the StoredPassword created.
     * @param website website of the StoredPassword created.
     * @param password password of the StoredPassword created.
     */
    public StoredPassword(String title, String website, String password)
    {
        this.setId(++totalIds);
        this.setTitle(title);
        this.setWebsite(website);
        this.setPassword(password);
        this.setLastUpdated();
    }

    /**
     * Gets the StoredPassword id.
     * @return id of the StoredPassword object.
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Validates the passed id. Throws IllegalArgumentException if doesn't pass.
     * @param id id to be validated
     */
    public static void validateId(int id)
    {
        if (id < 1)
        {
            throw new IllegalArgumentException("ID must be greater than 0.");
        }
    }

    /**
     * Sets and validates StoredPassword's id.
     * @param id id to be set to.
     */
    public void setId(int id)
    {
        validateId(id);
        if (id > totalIds)
        {
            totalIds = id;
        }
        this.id = id;
        this.setLastUpdated();
    }

    /**
     * Gets the StoredPassword title.
     * @return StoredPassword title.
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Validates and sanitizes the passed in title. Throws an 
     * IllegalArgumentException if a criteria is broken.
     * @param title title to be validated.
     * @return sanitized and validated String.
     */
    public static String validateTitle(String title)
    {
        if (title == null)
        {
            throw new IllegalArgumentException("Title cannot be null");
        }
        title = title.replaceAll("[\n\r]", "").trim();
        if (title.length() > 255)
        {
            throw new IllegalArgumentException("Title must be smaller than 255 characters.");
        }
        return title;
    }

    /**
     * Sets and validates StoredPassword's title.
     * @param title title to be set to StoredPassword.
     */
    public void setTitle(String title)
    {
        if (title == null)
        {
            throw new IllegalArgumentException("Title cannot be null.");
        }
        title = validateTitle(title);
        this.title = title;
        this.setLastUpdated();
    }

    /**
     * Gets the StoredPassword website.
     * @return StoredPassword website.
     */
    public String getWebsite()
    {
        return this.website;
    }

    /**
     * Validates and sanitizes the passed in website. Throws an 
     * IllegalArgumentException if a criteria is broken.
     * @param website website to be validated
     * @return sanitized and validated String.
     */
    public static String validateWebsite(String website)
    {
        if (website == null)
        {
            throw new IllegalArgumentException("Website cannot be null.");
        }
        website = website.replaceAll("[\n\r]", "").trim();
        Pattern urlRegex = Pattern.compile("^(https?://)?([a-zA-Z0-9.-]+)(:[0-9]{1,4})?$");
        Matcher matcher = urlRegex.matcher(website);
        if (!matcher.matches())
        {
            throw new IllegalArgumentException("Invalid URL.");
        }
        String hostname = matcher.group(2);
        if (hostname.startsWith("."))
        {
            throw new IllegalArgumentException("Invalid URL. Can't have a dot at the start of site hostname.");
        }
        if (hostname.matches("(\\.){2,}"))
        {
            throw new IllegalArgumentException("Invalid URL. Can't have 2 consecutive dots in the hostname.");
        }
        if (hostname.endsWith("."))
        {
            throw new IllegalArgumentException("Invalid URL. Can't have a dot at the end of site hostname.");
        }
        return website;
    }

    /**
     * Sets and validates StoredPassword's website.
     * @param website website to be set to.
     */
    public void setWebsite(String website)
    {
        if (website == null)
        {
            throw new IllegalArgumentException("Website cannot be null.");
        }
        validateWebsite(website);
        this.website = website;
        this.setLastUpdated();
    }

    /**
     * Gets String of the StoredPassword's password.
     * @return StoredPassword's password.
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * Validates the passed in password. Throws an IllegalArgumentException 
     * or a Password exception if a criteria is broken.
     * @param password password to be validated.
     */
    public static void validatePassword(String password)
    {
        if (password == null)
        {
            throw new IllegalArgumentException("Password cannot be null.");
        }
        if (password.isEmpty())
        {
            throw new IllegalArgumentException("Password can not be empty.");
        }
        checkPasswordStrength(password); // Throws PasswordException on fail
    }

    /**
     * Sets and validates StoredPassword's password.
     * @param password password to be set to.
     */
    public void setPassword(String password)
    {
        if (password == null)
        {
            throw new IllegalArgumentException("Password cannot be null.");
        }
        validatePassword(password);
        this.password = password;
        this.setLastUpdated();
    }

    /**
     * Gets the last time the StoredPassword was updated.
     * @return LocalDateTime object of the last updated time and date.
     */
    public LocalDateTime getLastUpdated()
    {
        return this.lastUpdated;
    }

    /**
     * Sets and validates the last time updated.
     * @param lastUpdated LocalDateTime to be set to.
     */
    public void setLastUpdated(LocalDateTime lastUpdated)
    {
        if (lastUpdated == null)
        {
            throw new IllegalArgumentException("Last Updated can not be null.");
        }
        this.lastUpdated = lastUpdated;
    }

    /**
     * Sets the last time updated to current time.
     */
    public void setLastUpdated()
    {
        this.lastUpdated = LocalDateTime.now();
    }

    @Override
    /**
     * toString() representation of the StoredPassword object.
     */
    public String toString()
    {
        return "StoredPassword{" + "id=" + id + ", title=" + title + ", website=" + website + ", password=" + password + ", lastUpdated=" + lastUpdated + '}';
    }

    /**
     * Turns all parameters of StoredPassword into a CSV formatted line.
     * @return String of the CSV line.
     */
    public String toCSVLine()
    {
        String sanitizedTitle = this.getTitle().replaceAll(",", "&#44;").replaceAll("\n", "&#10;").replaceAll("\r", "&#13;");
        String sanitizedWebsite = this.getWebsite().replaceAll(",", "&#44;").replaceAll("\n", "&#10;").replaceAll("\r", "&#13;");
        String sanitizedPassword = this.getPassword().replaceAll(",", "&#44;").replaceAll("\n", "&#10;").replaceAll("\r", "&#13;");
        String sanitizedLastUpdated = this.getLastUpdated().toString().replaceAll(",", "&#44;").replaceAll("\n", "&#10;").replaceAll("\r", "&#13;");
        return this.getId() + "," + sanitizedTitle + "," + sanitizedWebsite + "," + sanitizedPassword + "," + sanitizedLastUpdated;
    }

}
