package org.fer.syncfiles;

import java.io.FileReader;
import java.util.Properties;

/**
 * Created by fer on 19/01/15.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileReader("./test.properties"));

        while (true) {
            System.out.println("Properties : " + properties.getProperty("prop"));
            Thread.currentThread().sleep(5000);
        }
    }

}
