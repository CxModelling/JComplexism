package org.twz.io;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/10/13.
 */
public class FnJSON {
    public static List<Object> toObjectList(JSONArray js) throws JSONException {
        List<Object> arr = new ArrayList<>();
        for (int i=0; i<js.length(); i++) {
            arr.add(js.get(i));
        }
        return arr;
    }

    public static List<Double> toDoubleList(JSONArray js) throws JSONException {
        List<Double> arr = new ArrayList<>();
        for (int i=0; i<js.length(); i++) {
            arr.add(js.getDouble(i));
        }
        return arr;
    }

    public static List<String> toStringList(JSONArray js) throws JSONException {
        List<String> arr = new ArrayList<>();
        for (int i=0; i<js.length(); i++) {
            arr.add(js.getString(i));
        }
        return arr;
    }

    public static Set<Object> toObjectSet(JSONArray js) throws JSONException {
        Set<Object> arr = new HashSet<>();
        for (int i=0; i<js.length(); i++) {
            arr.add(js.get(i));
        }
        return arr;
    }

    public static Set<Double> toDoubleSet(JSONArray js) throws JSONException {
        Set<Double> arr = new HashSet<>();
        for (int i=0; i<js.length(); i++) {
            arr.add(js.getDouble(i));
        }
        return arr;
    }

    public static Set<String> toStringSet(JSONArray js) throws JSONException {
        Set<String> arr = new HashSet<>();
        for (int i=0; i<js.length(); i++) {
            arr.add(js.getString(i));
        }
        return arr;
    }

    public static Object[] toObjectArray(JSONArray js) throws JSONException {
        Object[] arr = new Object[js.length()];
        for (int i=0; i<js.length(); i++) {
            arr[i] = js.get(i);
        }
        return arr;
    }

    public static double[] toDoubleArray(JSONArray js) throws JSONException {
        double[] arr = new double[js.length()];
        for (int i=0; i<js.length(); i++) {
            arr[i] = js.getDouble(i);
        }
        return arr;
    }

    public static String[] toStringArray(JSONArray js) throws JSONException {
        String[] arr = new String[js.length()];
        for (int i=0; i<js.length(); i++) {
            arr[i] = js.getString(i);
        }
        return arr;
    }

    public static Map<String, Object> toObjectMap(JSONObject js) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        Iterator it = js.keys();
        String k;
        while(it.hasNext()) {
            k = it.next().toString();
            map.put(k, js.get(k));
        }
        return map;
    }

    public static Map<String, Double> toDoubleMap(JSONObject js) throws JSONException {
        Map<String, Double> map = new HashMap<>();
        Iterator it = js.keys();
        String k;
        while(it.hasNext()) {
            k = it.next().toString();
            map.put(k, js.getDouble(k));
        }
        return map;
    }

    public static Map<String, String> toStringMap(JSONObject js) throws JSONException {
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
