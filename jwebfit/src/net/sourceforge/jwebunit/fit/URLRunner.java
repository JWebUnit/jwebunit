/*
 * User: djoiner
 * Date: Oct 22, 2002
 * Time: 10:29:33 PM
 */
package net.sourceforge.jwebunit.fit;

import java.io.*;
import java.net.URL;

public class URLRunner {
    public static void main(String[] args) throws IOException {
        InputStream stream = null;
        FileOutputStream fos = null;
        File in = new File("in.txt");
        try {
            URL url = new URL(args[0]);
            stream = url.openStream();
            fos = new FileOutputStream(in);
            int i;
            while ((i = stream.read()) != -1) {
                fos.write(i);
            }
            fos.flush();
            fos.close();
            new FileRunner(in, new File("out.txt")).run();
            FileReader reader = new FileReader("out.txt");
            System.out.println(reader.toString());
        } finally {
            if (stream != null) stream.close();
        }
    }
}
