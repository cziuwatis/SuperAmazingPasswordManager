
/**
 *
 * @author
 */
public class PasswordWebsiteProperty implements PasswordOptions
{

    @Override
    public void setProperty(Object obj, String newProperty)
    {
        StoredPassword sp = (StoredPassword) obj;
        sp.setWebsite(newProperty);
    }

}
