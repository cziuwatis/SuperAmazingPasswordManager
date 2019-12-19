/**
 *
 * @author
 */
public class TitleSearchFilter implements PasswordSearchFilter
{
    @Override
    public String getProperty(Object obj)
    {
        return ((StoredPassword) obj).getTitle();
    }
}
