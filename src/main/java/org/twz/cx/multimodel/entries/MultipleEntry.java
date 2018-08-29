package org.twz.cx.multimodel.entries;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.mcore.IY0;
import org.twz.dataframe.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by TimeWz on 2017/11/14.
 */
public class MultipleEntry implements IModelEntry {
    private final String Prefix;
    private final String ModelProto;
    private IY0 Y0;
    private int From, To, By;


    public MultipleEntry(String prefix, String modelProto, IY0 y0, int from, int to, int by) {
        Prefix = prefix;
        ModelProto = modelProto;
        Y0 = y0;
        From = from;
        To = to;
        By = by;
    }

    public MultipleEntry(String prefix, String modelProto, IY0 y0, int from, int to) {
        this(prefix, modelProto, y0, from, to, 1);
    }

    public MultipleEntry(String prefix, String modelProto, IY0 y0, int to) {
        this(prefix, modelProto, y0, 1, to, 1);
    }


    @Override
    public String getProtoName() {
        return ModelProto;
    }

    @Override
    public List<Tuple<String, String, IY0>> generate() {
        List<Tuple<String, String, IY0>> ent = new ArrayList<>();
        String name;
        for (int i = From; i <= To; i+= By) {
            name = Prefix + "_" + i;
            ent.add(new Tuple<>(name, ModelProto, Y0));
        }
        return ent;
    }

    @Override
    public int size() {
        int k = 0;
        for (int i = From; i <= To; i+= By) {
            k ++;
        }
        return k;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Prefix", Prefix);
        js.put("Prototype", ModelProto);
        js.put("Y0", Y0.toJSON());
        js.put("From", From);
        js.put("To", To);
        js.put("By", By);
        return js;
    }

    @Override
    public String toString() {
        return String.format("Model: %s %d:%d:%d (%s)", Prefix, From, By, To, ModelProto);
    }
}
