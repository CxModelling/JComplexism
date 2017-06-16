package main;

import java.io.*;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */

public class Utils {
    public static String loadText(String path) {
        try {
            FileReader fr = new FileReader(path);

            BufferedReader br = new BufferedReader(fr);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            fr.close();
            br.close();
            return sb.toString();
        } catch(FileNotFoundException ex) {
            System.out.println("Unable to find file '" + path + "'");
        } catch(IOException ex) {
            System.out.println("Error reading file '" + path + "'");
        }
        return "";
    }
}
