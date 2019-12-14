
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author andrz
 */
public class test
{

    public static void main(String[] args)
    {
//        Scanner scan = new Scanner("a,,c,d");
//        scan.useDelimiter(",");
//        System.out.println(scan.next());
//        System.out.println(scan.next());
//        System.out.println(scan.next());
//        System.out.println(scan.next());
        String key = "Ry1LYVBkU2dWa1lwM3M2djl5L0I/RShIK01iUWVUaFc=";
//        PasswordStorage pstest = new PasswordStorage();
//        pstest.addNewPassword("title", "google.com", "veryStrongPassword123@@13123!!!!infinityandBEYOND");
//        pstest.addNewPassword("title", "google.com", "veryStrongPassword123@@13123!!!!infinityandBEYOND");
//        pstest.addNewPassword("ti,,,,,,,,,,,,,,,\n\ntle", "google.com", "TestingCommas,,haha,213@@@!!213,");
//        pstest.readPasswordsOut("passwordStore.txt", key);
//        PasswordStorage pstest2 = new PasswordStorage();
//        pstest2.readPasswordsIn("passwordStore.txt", key);
//        System.out.println(pstest2.getUserPasswords());

        Menu test = new Menu();
        test.run(key);
    }
}
