package org.twz.dataframe;

import org.json.JSONArray;
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
    private String Key;

    public DataFrame(List<Map<String, Double>> dat, String key) {
        Key = key;
        Data = new LinkedHashMap<>();

        List<String> cols = new ArrayList<>();
        dat.forEach(d->d.keySet().stream().filter(c->!cols.contains(c)).forEach(cols::add));

        Data.put(Key, dat.stream().map(e->e.get(Key)).collect(Collectors.toList()));
        cols.stream().filter(col -> !col.equals(Key))
                     .forEach(col -> Data.put(col, dat.stream()
                             .map(e -> e.getOrDefault(col, 0.0))
                     .collect(Collectors.toList())));
    }

    public void toCSV(String path) {
        StringBuilder sb = new StringBuilder();

        sb.append(Data.keySet().stream().collect(Collectors.joining(",")));
        int size = Data.get(Key).size();

        for (int i=0; i < size; i++) {
            sb.append("\n");
            int finalI = i;
            sb.append(Data.values().stream().map(e-> ""+e.get(finalI)).collect(Collectors.joining(",")));
        }
        IO.writeText(sb.toString(), path);
    }

    public void toJSON(String path) {
        IO.writeText(toJSON().toString(), path);
    }

    public JSONArray toJSON() {
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
        StringBuilder sb = new StringBuilder();
        sb.append(Key);
        sb.append("\t");
        sb.append(Data.keySet().stream()
                .filter(k-> !k.equals(Key))
                .collect(Collectors.joining("\t")));
        sb.append("\n");
        int size = Data.get(Key).size();

        for (int i=0; i < size; i++) {
            sb.append(Data.get(Key).get(i));

            for (Map.Entry<String, List<Double>> entry : Data.entrySet()) {
                if (!entry.getKey().equals(Key)) {
                    sb.append("\t");
                    sb.append(entry.getValue().get(i));
                }
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }
}
