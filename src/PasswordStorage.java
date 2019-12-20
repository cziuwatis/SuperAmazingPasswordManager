
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
 * PasswordStorage class.
 *
 * <p>
 * Description here.
 *
 * @author Luke Halpenny & Andrej Gorochov
 * @version 1.0
 */
public class PasswordStorage {

    private ArrayList<StoredPassword> userPasswords;

    public PasswordStorage() {
        this.userPasswords = new ArrayList<>();
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
            return false;
        }
        return true;
    }

    public boolean readPasswordsIn(String filePath, String key) {

        File file = new File(filePath);
        try (
                Scanner fileIn = new Scanner(file);
                Scanner source = new Scanner(Cipher.decryptString(fileIn.nextLine(), key))
        ) {
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
                } catch (NoSuchElementException | IllegalArgumentException | DateTimeParseException e) {
                    if (source.hasNextLine()) {
                        source.nextLine();
                    }
                }
            }
        } catch (FileNotFoundException | NoSuchElementException | IllegalArgumentException | CipherException e) {
            return false;
        }
        return true;
    }
}
