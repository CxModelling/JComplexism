package org.twz.io;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

/**
 *
 * Created by TimeWz on 2015/12/4.
 */
public class IO {
    public static int DoublePrecision = 4;

    public static String doubleFormat(double d, int i) {
        return String.format("%" + i + "g", d);
    }

    public static String doubleFormat(double d) {
        return doubleFormat(d, DoublePrecision);
    }

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
        } catch(IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static void writeJSON(JSONArray data, String file){
        try  {
            FileWriter f = new FileWriter(file);
            data.write(f);
            f.flush();
            f.close();
        } catch(IOException | JSONException e) {
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

    public static JSONObject loadJSON(String path) throws JSONException {
        return new JSONObject(loadText(path));
    }

    public static Map<String, List<String>> loadCSV(String path) {
        String[] texts = loadText(path).split("\n");
        int length = texts.length - 1;
        String[] cols = texts[0].split(",");

        for (int i = 0; i < cols.length; i++) {
            cols[i] = cols[i].replaceAll("\"", "");
        }

        int nc = cols.length;

        Map<String, List<String>> csv = new LinkedHashMap<>();
        for (String col: cols) {
            csv.put(col, new ArrayList<>());
        }
        String[] data;
        for (int i = 0; i < length; i++) {
            data = texts[i+1].split(",");
            for (int j = 0; j < nc; j++) {
                csv.get(cols[j]).add(data[j]);
            }
        }
        return csv;
    }

    public static void checkDirectory(String path) {
        File directory = new File(path);
        if (! directory.exists()){
            directory.mkdir();
        }
    }

}
