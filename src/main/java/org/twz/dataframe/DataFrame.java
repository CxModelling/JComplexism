package org.twz.dataframe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.io.IO;
import org.twz.io.AdapterJSONArray;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class DataFrame implements AdapterJSONArray {
    private Map<String, List<Double>> Data;
    private List<String> ColumnNames;
    private String Key;

    public DataFrame(List<Map<String, Double>> dat, String key) {
        Key = key;
        Data = new LinkedHashMap<>();

        List<String> cols = new ArrayList<>();
        cols.add(Key);
        dat.forEach(d->d.keySet().stream().filter(c->!cols.contains(c)).forEach(cols::add));
        ColumnNames = cols;

        Data.put(Key, dat.stream().map(e->e.get(Key)).collect(Collectors.toList()));
        cols.stream().filter(col -> !col.equals(Key))
                     .forEach(col -> Data.put(col, dat.stream()
                             .map(e -> e.getOrDefault(col, 0.0))
                     .collect(Collectors.toList())));
    }

    public void toCSV(String path) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.join(",", Data.keySet()));
        int size = Data.get(Key).size();

        for (int i=0; i < size; i++) {
            sb.append("\n");
            int finalI = i;
            sb.append(Data.values().stream().map(e-> ""+e.get(finalI)).collect(Collectors.joining(",")));
        }
        IO.writeText(sb.toString(), path);
    }

    public void toJSON(String path) throws JSONException {
        IO.writeText(toJSON().toString(), path);
    }

    public JSONArray toJSON() throws JSONException {
        JSONArray js = new JSONArray();
        int size = Data.get(Key).size();


        JSONObject temp;
        for (int i=0; i < size; i++) {
            temp = new JSONObject();
            for (Map.Entry<String, List<Double>> entry : Data.entrySet()) {
                temp.put(entry.getKey(), entry.getValue().get(i));
            }
            js.put(temp);
        }
        return js;
    }

    public void print() {
        print(10);
    }

    public void print(int n) {
        System.out.format(getHeadingFormat(n), ColumnNames.toArray());
        System.out.println();

        int size = Data.get(Key).size();

        String k;

        for (int i=0; i < size; i++) {
            for (int j = 0; j < ColumnNames.size(); j++) {
                k = ColumnNames.get(j);
                System.out.print(String.format(("%" + n + "g "), Data.get(k).get(i)));
            }

            System.out.println();
        }
    }

    private String getHeadingFormat(int n) {
        StringBuilder sb = new StringBuilder();
        sb.append("%").append(n).append("s");
        for (int i = 0; i < ColumnNames.size() - 1; i++) {
            sb.append(" ").append("%").append(n).append("s");
        }
        return sb.toString();
    }

}
