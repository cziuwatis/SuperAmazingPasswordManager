/**
 * Application class.
 *
 * <p>Description here.
 *
 * @author Luke Halpenny & Andrej Gorochov
 * @version 1.0
 */
public class Application {

    private Terminal terminal;
    private Login login;
    private Menu menu;

    public Application() {
        this.terminal = new Terminal();
        this.login = new Login(terminal);
        this.menu = new Menu();
    }

    public void run() {
        String key = login.login();
        if (key == null) {
            terminal.error("Couldn't log in!\n");
            terminal.error("Exiting...\n");
            System.exit(1);
        }
        menu.run(key);
    }

}
