
/**
 * Filter for getting website string.
 * @author Luke Halpenny & Andrej Gorochov
 */
public class WebsiteSearchFilter implements PasswordSearchFilter {

    @Override
    public String getProperty(Object obj) {
        return ((StoredPassword) obj).getWebsite();
    }
}
