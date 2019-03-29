package org.twz.datafunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.io.AdapterJSONObject;

import java.util.*;

public class DataCentre implements AdapterJSONObject {

    private Map<String, AbsDataFunction> DataSets;

    public DataCentre() {
        DataSets = new HashMap<>();
    }

    public DataCentre(JSONObject json) throws JSONException {
        this();
        JSONArray ja =json.getJSONArray("Entries");
        for (int i = 0; i < ja.length(); i++) {
            putJSON(ja.getJSONObject(i));
        }

    }

    public void readRawCSV(String Name, String Type, String filePath) {
        // todo
    }

    public void readRawJSON(String Name, String Type, String filePath) {
        // todo
    }

    public void readJSON(String json) {
        // todo

    }

    public void put(AbsDataFunction df) {
        DataSets.put(df.getName(), df);
    }

    public void putJSON(JSONObject json) {
        // todo
    }


    @Override
    public JSONObject toJSON() throws JSONException {
        JSONArray ds = new JSONArray();
        DataSets.values().forEach(e-> {
            try {
                ds.put(e.toJSON());
            } catch (JSONException ignored) {

            }
        });
        JSONObject js = new JSONObject();
        js.put("Type", "DataSets");
        js.put("Entries", ds);
        return js;
    }
}
