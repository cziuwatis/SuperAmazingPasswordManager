
/**
 *
 * @author
 */
public class PasswordTitleProperty implements PasswordOptions
{

    @Override
    public void setProperty(Object obj, String newProperty)
    {
        StoredPassword sp = (StoredPassword) obj;
        sp.setTitle(newProperty);
    }

}
