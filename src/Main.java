import java.util.Base64;

/**
 * Main class.
 *
 * <p>Description here.
 *
 * @author Luke Halpenny & Andrej Gorochov
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {
        Application app = new Application();
        app.run();
    }

//    public static void main(String[] args) {
//        byte[] saltBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
//        String salt = Base64.getEncoder().encodeToString(saltBytes);
//        Password password = new Password("testPassword2020!", salt);
//        String hash = password.generateHash();
//        System.out.println(hash);
//    }

}
