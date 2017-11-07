package utils.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/10/13.
 */
public class FnJSON {
    public static List<Object> toObjectList(JSONArray js) {
        List<Object> arr = new ArrayList<>();
        for (int i=0; i<js.length(); i++) {
            arr.add(js.get(i));
        }
        return arr;
    }

    public static List<Double> toDoubleList(JSONArray js) {
        List<Double> arr = new ArrayList<>();
        for (int i=0; i<js.length(); i++) {
            arr.add(js.getDouble(i));
        }
        return arr;
    }

    public static List<String> toStringList(JSONArray js) {
        List<String> arr = new ArrayList<>();
        for (int i=0; i<js.length(); i++) {
            arr.add(js.getString(i));
        }
        return arr;
    }

    public static Object[] toObjectArray(JSONArray js) {
        Object[] arr = new Object[js.length()];
        for (int i=0; i<js.length(); i++) {
            arr[i] = js.get(i);
        }
        return arr;
    }

    public static double[] toDoubleArray(JSONArray js) {
        double[] arr = new double[js.length()];
        for (int i=0; i<js.length(); i++) {
            arr[i] = js.getDouble(i);
        }
        return arr;
    }

    public static String[] toStringArray(JSONArray js) {
        String[] arr = new String[js.length()];
        for (int i=0; i<js.length(); i++) {
            arr[i] = js.getString(i);
        }
        return arr;
    }

    public static Map<String, Object> toObjectMap(JSONObject js) {
        Map<String, Object> map = new HashMap<>();
        Iterator it = js.keys();
        String k;
        while(it.hasNext()) {
            k = it.next().toString();
            map.put(k, js.get(k));
        }
        return map;
    }

    public static Map<String, Double> toDoubleMap(JSONObject js) {
        Map<String, Double> map = new HashMap<>();
        Iterator it = js.keys();
        String k;
        while(it.hasNext()) {
            k = it.next().toString();
            map.put(k, js.getDouble(k));
        }
        return map;
    }

    public static Map<String, String> toStringMap(JSONObject js) {
        Map<String, String> map = new HashMap<>();
        Iterator it = js.keys();
        String k;
        while(it.hasNext()) {
            k = it.next().toString();
            map.put(k, js.getString(k));
        }
        return map;
    }



}
