package org.twz.dataframe;

import org.json.JSONObject;
import org.twz.io.IO;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * Created by TimeWz on 2016/7/13.
 */
public class DataTable extends TreeMap<Double, DataEntry> implements Cloneable{

    private String PrimaryKey;

    public DataTable(String key) {
        PrimaryKey = key;
    }

    public DataTable() {
        this("Time");
    }

    public void outputCSV(String file) {
        StringBuilder s = new StringBuilder();
        s.append(PrimaryKey+",").append(this.firstEntry().getValue().header());
        for (Map.Entry<Double, DataEntry> de: this.entrySet()) {
            s.append("\n").append(de.getKey()).append(",").append(de.getValue().toCSV());
        }
        IO.writeText(s.toString(), file);
    }

    public void outputJSON(String file) {
        IO.writeJSON(new JSONObject(this), file);
    }


    public DataTable clone() {
        DataTable dt;
        try {
            dt = (DataTable) super.clone();
        } catch (Exception e) {
            dt = new DataTable(PrimaryKey);
            for (Map.Entry<Double, DataEntry> de: this.entrySet()) {
                dt.put(de.getKey(), de.getValue().clone());
            }
        }


        return dt;
    }
}
