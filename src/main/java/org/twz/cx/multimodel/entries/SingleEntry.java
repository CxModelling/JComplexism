package org.twz.cx.multimodel.entries;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.mcore.IY0;
import org.twz.dataframe.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TimeWz on 2017/11/14.
 */
public class SingleEntry implements IModelEntry {
    private final String Name;
    private final String ModelProto;
    private IY0 Y0;

    public SingleEntry(String name, String modelProto, IY0 y0) {
        Name = name;
        ModelProto = modelProto;
        Y0 = y0;
    }


    @Override
    public List<Tuple<String, String, IY0>> generate() {
        List<Tuple<String, String, IY0>> ent = new ArrayList<>();
        ent.add(new Tuple<>(Name, ModelProto, Y0));
        return ent;
    }

    @Override
    public String getProtoName() {
        return ModelProto;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Name", Name);
        js.put("Prototype", ModelProto);
        js.put("Y0", Y0.toJSON());
        return js;
    }

    @Override
    public String toString() {
        return String.format("Model: %s (%s)", Name, ModelProto);
    }
}
