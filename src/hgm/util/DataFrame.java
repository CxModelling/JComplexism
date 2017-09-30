package hgm.util;

import org.json.JSONArray;
import org.json.JSONString;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class DataFrame implements JSONString {
    private Map<String, List<Double>> Data;
    private String Key;

    public DataFrame(List<Map<String, Double>> dat, String key) {
        Key = key;
        Data = new LinkedHashMap<>();

        List<String> cols = new ArrayList<>(dat.get(0).keySet());
        Data.put(Key, dat.stream().map(e->e.get(Key)).collect(Collectors.toList()));
        cols.stream().filter(col -> !col.equals(Key))
                     .forEach(col -> Data.put(col, dat.stream().map(e -> e.get(col))
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

        IO.save(path, sb.toString());
    }

    public void toJSON(String path) {
        IO.save(path, toJSON().toString());
    }

    public JSONArray toJSON() {
        JSONArray js = new JSONArray();
        int size = Data.get(Key).size();

        for (int i=0; i < size; i++) {
            int finalI = i;
            js.put(Data.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e->e.getValue().get(finalI))));
        }
        return js;
    }

    public void print() {
        System.out.println(Data.keySet().stream().collect(Collectors.joining("\t")));
        int size = Data.get(Key).size();

        for (int i=0; i < size; i++) {
            int finalI = i;
            System.out.println(Data.values().stream().map(e-> String.format( "%5.3f", e.get(finalI))).collect(Collectors.joining("\t")));
        }


    }

    @Override
    public String toJSONString() {
        return toJSON().toString();
    }
}
