package org.twz.cx.multimodel.entries;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.io.AdapterJSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Entry defined with a set of models
 * Created by TimeWz on 2017/11/14.
 */
public class RelationEntry implements AdapterJSONObject {
    public final String Selector, Parameter;
    private boolean Single;


    public RelationEntry(String s) {
        s = s.replaceAll("\\s+", "");
        String[] ss = s.split("@");
        Selector = ss[0];
        Parameter = ss[1];

        Pattern pat = Pattern.compile("\\A\\w+\\Z");
        Matcher mat = pat.matcher(Selector);
        Single = mat.find();

    }

    public RelationEntry(JSONObject js) throws JSONException {
        Selector = js.getString("Selector");
        Single = js.getString("Type").equals("Single");
        Parameter = js.getString("Parameter");
    }


    public String toString() {
        return "Selector: "+Selector+
                ", Type: "+ ((Single)? "Single": "Multiple")+
                ", Parameter: "+ Parameter;
    }

    public boolean isSingle() {
        return Single;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        return new JSONObject("{'Selector': " + Selector +
                ", 'Type': " + ((Single)? "Single": "Multiple") +
                ", 'Parameter': " + Parameter + "}");
    }

}
