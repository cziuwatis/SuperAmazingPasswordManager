import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * StoredPassword class.
 *
 * <p>Description here.
 *
 * @author Luke Halpenny
 * @version 1.0
 */
public class StoredPassword {

    private int id;
    private String title;
    private String website;
    private String password;
    private LocalDateTime lastUpdated;

    public static boolean checkPasswordStrength(String password) {
        if(password.length() < 8) {
            return false;
        }
        return true; //TODO check password strength
    }

    public StoredPassword(int id, String title, String website, String password, LocalDateTime lastUpdated) {
        this.setId(id);
        this.setTitle(title);
        this.setWebsite(website);
        this.setPassword(password);
        this.setLastUpdated();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        if(id < 1) {
            throw new IllegalArgumentException("ID must be greater than 0.");
        }
        this.id = id;
        this.setLastUpdated();
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        if(title.length() > 255) {
            throw new IllegalArgumentException("Title must be smaller than 255 characters.");
        }
        this.title = title;
        this.setLastUpdated();
    }

    public String getWebsite() {
        return this.website;
    }

    public void setWebsite(String website) {
        Pattern urlRegex = Pattern.compile("^(https?://)?([a-zA-z0-9.-]+)(:[0-9]{1,4})?$");
        Matcher matcher = urlRegex.matcher(website);
        if(!matcher.matches()) {
            throw new IllegalArgumentException("Invalid URL.");
        }
        String hostname = matcher.group(2);
        for (int i = 0; i < hostname.length() - 1; i++) {
            if(hostname.charAt(i) == hostname.charAt(i+1)) {
                throw new IllegalArgumentException("Invalid URL.");
            }
        }
        this.website = website;
        this.setLastUpdated();
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        if(password.isEmpty()) {
            throw new IllegalArgumentException("Password can not be empty.");
        }
        if(!checkPasswordStrength(password)) {
            throw new IllegalArgumentException("Password too weak.");
        }
        this.password = password;
        this.setLastUpdated();
    }

    public LocalDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    private void setLastUpdated() { // Automated, doesn't need to be public
        this.lastUpdated = LocalDateTime.now();
    }

}
