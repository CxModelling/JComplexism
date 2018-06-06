package org.twz.cx.mcore;


import org.json.JSONObject;
import org.json.JSONString;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by timewz on 28/09/17.
 */
public class Meta implements JSONString{
    private String PC, DC, Prototype;
    private Map<String, Object> Arguments;

    public Meta(String pc, String dc, String proto) {
        PC = pc;
        DC = dc;
        Prototype = proto;
        Arguments = new HashMap<>();
    }

    public String getPC() {
        return PC;
    }

    public String getDC() {
        return DC;
    }

    public String getPrototype() {
        return Prototype;
    }

    public Object getArgument(String arg) {
        try {
            return Arguments.get(arg);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void setArguments(String arg, Object value) {
        Arguments.put(arg, value);
    }

    @Override
    public String toJSONString() {
        JSONObject js = new JSONObject();
        js.append("PC", PC);
        js.append("DC", DC);
        js.append("Prototype", Prototype);
        js.append("Arguments", Arguments);
        return js.toString();
    }
}
