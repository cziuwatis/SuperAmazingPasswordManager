
/**
 *
 * @author
 */
public class PasswordPasswordProperty implements PasswordOptions
{

    @Override
    public void setProperty(Object obj, String newProperty)
    {
        StoredPassword sp = (StoredPassword) obj;
        sp.setPassword(newProperty);
    }

}
