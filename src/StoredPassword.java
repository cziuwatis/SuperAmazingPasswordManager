
import java.security.SecureRandom;
import javax.rmi.CORBA.Util;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * StoredPassword class.
 *
 * <p>
 * Description here.
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

    public static String generateRandomPassword(int length)
    {
        SecureRandom rand = new SecureRandom();
        String pass = "";
        boolean isValidPassword = false;
        while (!isValidPassword)
        {
            pass = "";
            try
            {
                for (int i = 0; i < length; i++)
                {
                    pass += (char) (rand.nextInt(94) + 32);//32 start of ASCII characters, 94 difference between end of ASCI and start
                }
                StoredPassword.validatePassword(pass);
                isValidPassword = true;
            }
            catch (PasswordException e)
            {

            }
        }
        return pass;
    }

    public static String generateEasyToSayRandomPassword(int length)
    {
        SecureRandom rand = new SecureRandom();
        String pass = "";
        for (int i = 0; i < length; i++)
        {
            char passChar = (char) (rand.nextInt(26) + 65);//26 for alphabet size, 65 for start of ASCII characters
            if (rand.nextInt(100) < 50) //50/50 chance either capital or lowercase
            {
                passChar = Character.toLowerCase(passChar);
            }
            pass += passChar;
        }
        return pass;
    }

    public static String generateEasyToReadRandomPassword(int length)
    {
        SecureRandom rand = new SecureRandom();
        String pass = "";
        boolean isValidPassword = false;
        while (!isValidPassword)
        {
            pass = "";
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
                        pass += passChar;
                    }
                }
                StoredPassword.validatePassword(pass);
                isValidPassword = true;
            }
            catch (PasswordException e)
            {

            }
        }
        return pass;
    }

    public static boolean checkPasswordStrength(String password)
    {
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
        if (Utilities.isCommonPassword(password))
        {
            throw new PasswordException("Password must not match a common password.");
        }

        return true;
    }

    public static int getHighestTotalId()
    {
        return totalIds;
    }

    public StoredPassword(int id, String title, String website, String password, LocalDateTime lastUpdated)
    {
        this.setId(id);
        this.setTitle(title);
        this.setWebsite(website);
        this.setPassword(password);
        this.setLastUpdated(lastUpdated);
    }

    public StoredPassword(int id, String title, String website, String password)
    {
        this.setId(id);
        this.setTitle(title);
        this.setWebsite(website);
        this.setPassword(password);
        this.setLastUpdated();
    }

    public StoredPassword(String title, String website, String password)
    {
        this.setId(++totalIds);
        this.setTitle(title);
        this.setWebsite(website);
        this.setPassword(password);
        this.setLastUpdated();
    }

    public int getId()
    {
        return this.id;
    }

    public static void validateId(int id)
    {
        if (id < 1)
        {
            throw new IllegalArgumentException("ID must be greater than 0.");
        }
    }

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

    public String getTitle()
    {
        return this.title;
    }

    public static String validateTitle(String title)
    {
        title = title.replaceAll("[\n\r]", "").trim();
        if (title.length() > 255)
        {
            throw new IllegalArgumentException("Title must be smaller than 255 characters.");
        }
        return title;
    }

    public void setTitle(String title)
    {
        title = validateTitle(title);
        this.title = title;
        this.setLastUpdated();
    }

    public String getWebsite()
    {
        return this.website;
    }

    public static String validateWebsite(String website)
    {
        website = website.replaceAll("[\n\r]", "").trim();
        Pattern urlRegex = Pattern.compile("^(https?://)?([a-zA-Z0-9.-]+)(:[0-9]{1,4})?$");
        Matcher matcher = urlRegex.matcher(website);
        if (!matcher.matches())
        {
            throw new IllegalArgumentException("Invalid URL.");
        }
        String hostname = matcher.group(2);
        if (hostname.matches("(\\.){2,}"))
        {
            throw new IllegalArgumentException("Invalid URL. Can't have 2 consecutive dots in the hostname");
        }
        if (hostname.matches(".*\\.$"))
        {
            throw new IllegalArgumentException("Invalid URL. Can't have a dot at the end of site hostname");
        }
        return website;
    }

    public void setWebsite(String website)
    {
        validateWebsite(website);
        this.website = website;
        this.setLastUpdated();
    }

    public String getPassword()
    {
        return this.password;
    }

    public static void validatePassword(String password)
    {
        if (password.isEmpty())
        {
            throw new IllegalArgumentException("Password can not be empty.");
        }
        checkPasswordStrength(password); // Throws PasswordException on fail
    }

    public void setPassword(String password)
    {
        validatePassword(password);
        this.password = password;
        this.setLastUpdated();
    }

    public LocalDateTime getLastUpdated()
    {
        return this.lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated)
    {
        if (lastUpdated == null)
        {
            throw new IllegalArgumentException("Last Updated can not be null.");
        }
        this.lastUpdated = lastUpdated;
    }

    public void setLastUpdated()
    {
        this.lastUpdated = LocalDateTime.now();
    }

    @Override
    public String toString()
    {
        return "StoredPassword{" + "id=" + id + ", title=" + title + ", website=" + website + ", password=" + password + ", lastUpdated=" + lastUpdated + '}';
    }

    public String toCSVLine()
    {//I can probably make a string and just run the replaceAll once instead of one by one
        //though there might be other stuff we want to check for each one so I'll leave it like it is
        //Another note, even though we are removing \n and \r in our validation
        String sanitizedTitle = this.getTitle().replaceAll(",", "&#44;").replaceAll("\n", "&#10;").replaceAll("\r", "&#13;");
        String sanitizedWebsite = this.getWebsite().replaceAll(",", "&#44;").replaceAll("\n", "&#10;").replaceAll("\r", "&#13;");
        String sanitizedPassword = this.getPassword().replaceAll(",", "&#44;").replaceAll("\n", "&#10;").replaceAll("\r", "&#13;");
        String sanitizedLastUpdated = this.getLastUpdated().toString().replaceAll(",", "&#44;").replaceAll("\n", "&#10;").replaceAll("\r", "&#13;");
        return this.getId() + "," + sanitizedTitle + "," + sanitizedWebsite + "," + sanitizedPassword + "," + sanitizedLastUpdated;
    }

}
