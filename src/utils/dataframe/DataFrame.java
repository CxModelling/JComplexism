package utils.dataframe;

import org.json.JSONArray;
import utils.IO;
import utils.json.AdapterJSONArray;

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
        IO.writeText(sb.toString(), path);
    }

    public void toJSON(String path) {
        IO.writeText(toJSON().toString(), path);
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
}
