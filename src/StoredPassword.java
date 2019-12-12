
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * StoredPassword class.
 *
 * <p>
 * Description here.
 *
 * @author Luke Halpenny
 * @version 1.0
 */
public final class StoredPassword
{

    private static int totalIds = 0;
    private int id; //Snowflake?
    private String title;
    private String website;
    private String password;
    private LocalDateTime lastUpdated;

    public static boolean checkPasswordStrength(String password)
    {
        if (password.length() < 8)
        {
            return false;
        }
        return true; //TODO check password strength
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
        this.setLastUpdated(null);
    }

    public StoredPassword(String title, String website, String password)
    {
        this.setId(++totalIds);
        this.setTitle(title);
        this.setWebsite(website);
        this.setPassword(password);
        this.setLastUpdated(null);
    }

    public int getId()
    {
        return this.id;
    }

    public void setId(int id)
    {
        if (id < 1)
        {
            throw new IllegalArgumentException("ID must be greater than 0.");
        }
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

    public void setTitle(String title)
    {
        if (title.length() > 255)
        {
            throw new IllegalArgumentException("Title must be smaller than 255 characters.");
        }
        this.title = title;
        this.setLastUpdated();
    }

    public String getWebsite()
    {
        return this.website;
    }

    public void setWebsite(String website)
    {
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
        this.website = website;
        this.setLastUpdated();
    }

    public String getPassword()
    {
        return this.password;
    }

    public void setPassword(String password)
    {
        if (password.isEmpty())
        {
            throw new IllegalArgumentException("Password can not be empty.");
        }
        if (!checkPasswordStrength(password))
        {
            throw new IllegalArgumentException("Password too weak.");
        }
        this.password = password;
        this.setLastUpdated();
    }

    public LocalDateTime getLastUpdated()
    {
        return this.lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    public void setLastUpdated()
    {
        this.lastUpdated = LocalDateTime.now();
    }

}
