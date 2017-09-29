package hgm.util;

import org.json.JSONObject;

import java.io.*;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */

public class IO {
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


    public static void saveJSON(String path, JSONObject js) {
        save(path, js.toString());
    }

    public static void save(String path, String dat) {
        try {
            FileWriter writer = new FileWriter(path);
            writer.append(dat);
            writer.flush();
            writer.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
