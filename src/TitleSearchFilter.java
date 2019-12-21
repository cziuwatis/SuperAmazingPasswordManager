/**
 * Filter for getting title string.
 * @author Luke Halpenny & Andrej Gorochov
 */
public class TitleSearchFilter implements PasswordSearchFilter {
    @Override
    public String getProperty(Object obj) {
        return ((StoredPassword) obj).getTitle();
    }
}
