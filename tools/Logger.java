/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author xStr0nGx
 */
public class Logger {

    public static void log(String text) throws IOException {
        log(text.getBytes(), 1);
    }

    public static void log(byte[] text, int type) throws IOException {
        switch (type) {
            case 0:
                try {
                FileOutputStream out = new FileOutputStream("ErrorLogs.txt");
                out.write(text);
                out.close();
                } catch (FileNotFoundException fnfe) {
                    System.err.println(fnfe);
                }
                break;
            case 1:
                System.out.println(Arrays.toString(text));
                break;
            default:
                System.out.printf("Uncoded type has been found: %d", type);
                break;
        }
    }
}
