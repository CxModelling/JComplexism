package org.twz.io;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

/**
 *
 * Created by TimeWz on 2015/12/4.
 */
public class IO {
    public static void writeText(String data, String file){
        try {
            FileWriter writer = new FileWriter(file);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeJSON(JSONObject data, String file){
        try  {
            FileWriter f = new FileWriter(file);
            data.write(f);
            f.flush();
            f.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadText(String path) {
        try {
            return findText(path);
        } catch(FileNotFoundException ex) {
            try {
                path = new File(path).getCanonicalPath();
                return findText(path);
            } catch(FileNotFoundException exx) {
                System.out.println("File '" + path + "' does not exist");
            } catch (IOException e1) {
                System.out.println("Error reading file '" + path + "'");
            }
        } catch(IOException ex) {
            System.out.println("Error reading file '" + path + "'");
        }
        return "";
    }

    private static String findText(String path) throws IOException {
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
    }

    public static JSONObject loadJSON(String path) {
        return new JSONObject(loadText(path));
    }
}
