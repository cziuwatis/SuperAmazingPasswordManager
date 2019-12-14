import sun.rmi.runtime.Log;

import java.io.Console;
import java.util.Scanner;

/**
 * Main class.
 *
 * <p>Description here.
 *
 * @author Luke Halpenny
 * @version 1.0
 */
public class Main {

//    public static void main(String[] args) {
//        String password = "123456";
//        String salt = Password.generateRandomSalt();
//        String key = new Password(password, salt).generateHash();
//
//        String plaintext = "Hello, world!\nGoodbye,\tworld.";
//        System.out.println(plaintext);
//
//        String ciphertext = Cipher.encryptString(plaintext, key);
//        System.out.println(ciphertext);
//
//        String output = Cipher.decryptString(ciphertext, key);
//        System.out.println(output);
//    }

//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("\u001B[6n");
//        scanner.useDelimiter("R");
//        String test = scanner.next();
//        System.out.println(test);
//
//    }

    public static void main(String[] args) {
        Application app = new Application();
        app.run();
    }

}
