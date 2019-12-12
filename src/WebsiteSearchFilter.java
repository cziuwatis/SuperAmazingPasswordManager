/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andrz
 */
public class WebsiteSearchFilter implements PasswordSearchFilter
{

    @Override
    public String getProperty(Object obj)
    {
        return ((StoredPassword) obj).getWebsite();
    }
}
