
import java.io.File;
import java.io.FileNotFoundException;
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
 * Class used to deal with and store StoredPasswords.
 * </p>
 * 
 * @author Luke Halpenny & Andrej Gorochov
 * @version 1.0
 */
public class PasswordStorage {

    private ArrayList<StoredPassword> userPasswords;

    /**
     * Default constructor for PasswordStorage.
     */
    public PasswordStorage() {
        this.userPasswords = new ArrayList<>();
    }

    /**
     * Adds a new password to the list of passwords. Throws 
     * IllegalArgumentException and PasswordException if any of the fields are 
     * invalid.
     * @param title title of the new password entry
     * @param website website of the new password entry
     * @param password password of the new password entry
     */
    public void addNewPassword(String title, String website, String password) {
        this.userPasswords.add(new StoredPassword(title, website, password));
    }

    /**
     * Changes the specified entry's title to the new one.
     * @param id id of the StoredPassword to be changed.
     * @param newTitle the new title to be set.
     * @return true if changed, false if StoredPassword wasn't found.
     */
    public boolean editPasswordTitle(int id, String newTitle) {
        StoredPassword userPassword = findStoredPassword(id);
        if (userPassword != null) {
            userPassword.setTitle(newTitle);
            return true;
        }
        return false;
    }

    /**
     * Changes the specified entry's website to the new one.
     * @param id id of the StoredPassword to be changed.
     * @param newWebsite the new website to be set.
     * @return true if changed, false if StoredPassword wasn't found.
     */
    public boolean editPasswordWebsite(int id, String newWebsite) {
        StoredPassword userPassword = findStoredPassword(id);
        if (userPassword != null) {
            userPassword.setWebsite(newWebsite);
            return true;
        }
        return false;
    }

    /**
     * Changes the specified entry's password to the new one.
     * @param id id of the StoredPassword to be changed.
     * @param newPassword the new password to be set.
     * @return true if changed, false if StoredPassword wasn't found.
     */
    public boolean editPasswordPassword(int id, String newPassword) {
        StoredPassword userPassword = findStoredPassword(id);
        if (userPassword != null) {
            userPassword.setPassword(newPassword);
            return true;
        }
        return false;
    }

    //this method returns a new copy where details can't be edited to the existing one
    /**
     * Returns a copy of the specified StoredPassword 
     * (edited details won't be transfered to the existing StoredPassword).
     * @param id id of the StoredPassword to be gotten.
     * @return null if not found, otherwise a copy of the StoredPassword is 
     * returned.
     */
    public StoredPassword getPasswordDetails(int id) {
        StoredPassword userPassword = findStoredPassword(id);
        if (userPassword != null) {
            return new StoredPassword(userPassword.getId(), userPassword.getTitle(), userPassword.getWebsite(), userPassword.getPassword());
        }
        return null;
    }

    /**
     * Gets the StoredPassword's password.
     * @param id id of the StoredPassword to be gotten.
     * @return null if not found, otherwise the StoredPassword's password is 
     * returned.
     */
    public String getPassword(int id) {
        StoredPassword userPassword = findStoredPassword(id);
        if (userPassword != null) {
            return userPassword.getPassword();
        }
        return null;
    }

    /**
     * Gets the last added StoredPassword's id.
     * @return last added stored password's id. Returns -1 if no StoredPasswords
     * present.
     */
    public int getLatestPasswordId() {
        if (this.userPasswords.size() > 0) {
            return this.userPasswords.get(this.userPasswords.size() - 1).getId();
        }
        return -1;
    }

    /**
     * Removes the last added StoredPassword.
     */
    public void removeLatestPassword() {
        if (this.userPasswords.size() > 0) {
            this.userPasswords.remove(this.userPasswords.size() - 1);
        }
    }

    /**
     * Removes the specified StoredPassword from list.
     * @param id id of the StoredPassword to be removed.
     * @return true if found and removed, false if not found/not removed.
     */
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

    /**
     * Gets the entire list of StoredPasswords.
     * @return ArrayList of stored password.
     */
    public ArrayList<StoredPassword> getUserPasswords() {
        return this.userPasswords;
    }

    /**
     * Gets a list of StoredPasswords containing the searchString of the 
     * specified filter.
     * @param searchString string used to search for entries.
     * @param filter PasswordSearchFilter that gets the required property to 
     * search for.
     * @return a list of StoredPasswords matching the searchString. Empty if
     * none are found.
     */
    public ArrayList<StoredPassword> getUserPasswords(String searchString, PasswordSearchFilter filter) {
        ArrayList<StoredPassword> passwords = new ArrayList<>();
        for (StoredPassword userPassword : this.userPasswords) {
            if (filter.getProperty(userPassword).toLowerCase().contains(searchString.toLowerCase())) {
                passwords.add(userPassword);
            }
        }
        return passwords;
    }

    /**
     * Finds the specified StoredPassword by id.
     * @param id id of the StoredPassword which is being searched for.
     * @return null if not found, otherwise the specified StoredPassword is 
     * returned.
     */
    private StoredPassword findStoredPassword(int id) {
        for (StoredPassword userPassword : this.userPasswords) {
            if (userPassword.getId() == id) {
                return userPassword;
            }
        }
        return null;
    }

    /**
     * Checks whether the specified password is used in any of the entries.
     * @param password password to check for.
     * @return true if password has been used in at least one of the entries.
     */
    public boolean isPasswordUsed(String password) {
        for (StoredPassword userPassword : this.userPasswords) {
            if (userPassword.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether specified password is used two or more times.
     * @param password password to check for.
     * @return true if the password has been used in at least two entries.
     */
    public boolean isDuplicate(String password) {
        int count = 0;
        for (StoredPassword userPassword : this.userPasswords) {
            if (userPassword.getPassword().equals(password)) {
                count++;
                if(count >= 2) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Encrypts using AES and the specified key, and writes all 
     * storedPasswords into the specified file in filePath in CSV format.
     * @param filePath path of where to write the passwords to.
     * @param key key to be used to encrypt the string in file.
     * @return true if no exception occurred while writing out.
     */
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

    /**
     * Reads specified text file and decrypts it and adds each entry to the 
     * password storage. If there is an error with an entry, it skips it 
     * and goes on to the next one.
     * @param filePath path of where to read the password from.
     * @param key key to be used to decrypt the string in file.
     * @return true if file was found, encrypted string was decrypted
     * and it successfully read in.
     */
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
