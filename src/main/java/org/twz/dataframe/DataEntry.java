package org.twz.dataframe;

import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2016/7/13.
 */
public class DataEntry extends TreeMap<String, Double> {

    public DataEntry() {
        super();
    }

    public DataEntry(Map<String, Double> m) {
        super();
        putAll(m);
    }

    public String toCSV() {
        return this.values().stream()
                .map(v -> v+"")
                .collect(Collectors.joining(","));
    }

    public String header() {
        return this.keySet().stream()
                .collect(Collectors.joining(","));
    }

    public JSONObject toJSON() {
        return new JSONObject(this);
    }

    public DataEntry clone() {
        DataEntry de = new DataEntry();
        de.putAll(this);
        return de;
    }
}
