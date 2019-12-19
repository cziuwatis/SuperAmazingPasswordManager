
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author andrz
 */
public class PasswordStorage {

    private ArrayList<StoredPassword> userPasswords;

    public PasswordStorage() {
        this.userPasswords = new ArrayList<>();
    }

    public void addNewPassword(int id, String title, String website, String password, LocalDateTime lastUpdated) {
        this.userPasswords.add(new StoredPassword(id, title, website, password, lastUpdated));
    }

    public void addNewPassword(String title, String website, String password) {
        this.userPasswords.add(new StoredPassword(title, website, password));
    }

    public boolean editPasswordTitle(int id, String newTitle) {
        StoredPassword userPassword = findStoredPassword(id);
        if (userPassword != null) {
            userPassword.setTitle(newTitle);
            return true;
        }
        return false;
    }

    public boolean editPasswordWebsite(int id, String newWebsite) {
        StoredPassword userPassword = findStoredPassword(id);
        if (userPassword != null) {
            userPassword.setWebsite(newWebsite);
            return true;
        }
        return false;
    }

    public boolean editPasswordPassword(int id, String newPassword) {
        StoredPassword userPassword = findStoredPassword(id);
        if (userPassword != null) {
            userPassword.setPassword(newPassword);
            return true;
        }
        return false;
    }

    //    public String getPasswordWebsite(int id)
//    {
//        StoredPassword userPassword = findStoredPassword(id);
//        if (userPassword != null)
//        {
//            return userPassword.getWebsite();
//        }
//        return null;
//    }
    //this method returns a new copy where details can't be edited to the existing one
    public StoredPassword getPasswordDetails(int id) {
        StoredPassword userPassword = findStoredPassword(id);
        if (userPassword != null) {
            return new StoredPassword(userPassword.getId(), userPassword.getTitle(), userPassword.getWebsite(), userPassword.getPassword());
        }
        return null;
    }

    //this method returns the actual password where details can be edited
    public String getPassword(int id) {
        StoredPassword userPassword = findStoredPassword(id);
        if (userPassword != null) {
            return userPassword.getPassword();
        }
        return null;
    }

    public int getLatestPasswordId() {
        if (this.userPasswords.size() > 0) {
            return this.userPasswords.get(this.userPasswords.size() - 1).getId();
        }
        return -1;
    }

    public void removeLatestPassword() {
        if (this.userPasswords.size() > 0) {
            this.userPasswords.remove(this.userPasswords.size() - 1);
        }
    }

    public boolean removePassword(int id) {
        int i = 0;
        boolean isRemoved = false;
        while (!isRemoved && i < this.userPasswords.size()) {
            if (this.userPasswords.get(i).getId() == id) {
                this.userPasswords.remove(i);
                isRemoved = true;
            }
            i++;
        }
        return isRemoved;
    }

    public ArrayList<StoredPassword> getUserPasswords() {
        return this.userPasswords;
    }

    public ArrayList<StoredPassword> getUserPasswords(String searchString, PasswordSearchFilter filter) {
        ArrayList<StoredPassword> passwords = new ArrayList<>();
        for (StoredPassword userPassword : this.userPasswords) {
            if (filter.getProperty(userPassword).toLowerCase().contains(searchString.toLowerCase())) {
                passwords.add(userPassword);
            }
        }
        return passwords;
    }

    private StoredPassword findStoredPassword(int id) {
        for (StoredPassword userPassword : this.userPasswords) {
            if (userPassword.getId() == id) {
                return userPassword;
            }
        }
        return null;
    }

    public boolean isPasswordUsed(String password) {
        for (StoredPassword userPassword : this.userPasswords) {
            if (userPassword.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public boolean readPasswordsOut(String filePath, String key) {
        StringBuilder allStoredPasswords = new StringBuilder();
        for (StoredPassword userPassword : this.userPasswords) {
            allStoredPasswords.append(userPassword.toCSVLine()).append("\n");
        }
        allStoredPasswords = new StringBuilder(Cipher.encryptString(allStoredPasswords.toString(), key));
        try (FileWriter fileInput = new FileWriter(new File(filePath))) {
            fileInput.write(allStoredPasswords.toString());
        } catch (IOException e) {
            System.out.println(e.getMessage()); //@TODO use terminal
            return false;
        }
        return true;
    }

    public boolean readPasswordsIn(String filePath, String key) {

        try (Scanner source = new Scanner(Cipher.decryptString((new Scanner(new FileReader(filePath))).nextLine(), key)))//this ok? :D :D
        {
            source.useDelimiter("[,\n]");
            int passwordId;
            String title, website, password;
            LocalDateTime lastUpdated;
            while (source.hasNextLine() && source.hasNext()) {
                try {
                    passwordId = source.nextInt();
                    title = source.next().replaceAll("&#44;", ",").replaceAll("&#10;", "\n").replaceAll("&#13;", "\r");
                    website = source.next().replaceAll("&#44;", ",").replaceAll("&#10;", "\n").replaceAll("&#13;", "\r");
                    password = source.next().replaceAll("&#44;", ",").replaceAll("&#10;", "\n").replaceAll("&#13;", "\r");
                    lastUpdated = LocalDateTime.parse(source.next().replaceAll("&#44;", ",").replaceAll("&#10;", "\n").replaceAll("&#13;", "\r"));
                    this.userPasswords.add(new StoredPassword(passwordId, title, website, password, lastUpdated));
                } catch (NoSuchElementException | DateTimeParseException e) {
                    //@TODO do we want to send a message here?
                    System.out.println(e.getMessage());
                    if (source.hasNextLine()) {
                        source.nextLine();
                    }
                } catch (IllegalArgumentException e) {
                    //@TODO do we want to send a message that it's too weak or just keep it hidden?
                    System.out.println(e.getMessage());
                    if (source.hasNextLine()) {
                        source.nextLine();
                    }
                }
            }
        }//@TODO add catch for no line found
        catch (FileNotFoundException | NoSuchElementException | IllegalArgumentException | CipherException e) {
            //cipher exception for any exceptions in cipher that may occur
            //nosuchelementexception for the .nextLine() in try with resources
            //illegalargumentexception is for the base64 encoding
            System.out.println(e.getMessage());//@TODO remove sout
            return false;
        }
        return true;
    }

    //@TODO remove debug
    //debug purposes
//    public boolean readPasswordsOut2(String filePath, String key)
//    {
//        String allStoredPasswords = "";
//        for (StoredPassword userPassword : this.userPasswords)
//        {
//            allStoredPasswords += userPassword.toCSVLine() + "\n";
//        }
////        allStoredPasswords = Cipher.encryptString(allStoredPasswords, key);
//        try (FileWriter fileInput = new FileWriter(new File(filePath)))
//        {
//            fileInput.write(allStoredPasswords);
//            fileInput.close();
//        }
//        catch (IOException e)
//        {
//            System.out.println(e.getMessage());//@TODO use terminal
//            return false;
//        }
//        return true;
//    }
//
//    public boolean readPasswordsIn2(String filePath, String key)
//    {
//
//        try (Scanner source = new Scanner(new FileReader(filePath)))
//        {
//            source.useDelimiter("[,\n]");
//            int passwordId;
//            String title, website, password;
//            LocalDateTime lastUpdated;
//            while (source.hasNextLine() && source.hasNext())
//            {
//                try
//                {
//                    passwordId = source.nextInt();
//                    title = source.next().replaceAll("&#44;", ",").replaceAll("&#10;", "\n").replaceAll("&#13;", "\r");
//                    website = source.next().replaceAll("&#44;", ",").replaceAll("&#10;", "\n").replaceAll("&#13;", "\r");
//                    password = source.next().replaceAll("&#44;", ",").replaceAll("&#10;", "\n").replaceAll("&#13;", "\r");
//                    lastUpdated = LocalDateTime.parse(source.next().replaceAll("&#44;", ",").replaceAll("&#10;", "\n").replaceAll("&#13;", "\r"));
//                    this.userPasswords.add(new StoredPassword(passwordId, title, website, password, lastUpdated));
//                }
//                catch (NoSuchElementException | DateTimeParseException e)
//                {
//                    System.out.println(e.getMessage());
//                    if (source.hasNextLine())
//                    {
//                        source.nextLine();
//                    }
//                }
//                catch (IllegalArgumentException e)
//                {
//                    //@TODO do we want to send a message that it's too weak or just keep it hidden?
//                    if (source.hasNextLine())
//                    {
//                        source.nextLine();
//                    }
//                }
//            }
//        }//@TODO add catch for no line found
//        catch (FileNotFoundException e)
//        {
//            System.out.println(e.getMessage());//@TODO use terminal
//            return false;
//        }
//        return true;
//    }
}
