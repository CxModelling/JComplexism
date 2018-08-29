package org.twz.factory.arguments;

import org.twz.factory.Workshop;
import org.twz.io.FnJSON;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * Created by TimeWz on 2017/11/3.
 */
public class ProbTabArg extends AbsArgument{
    public ProbTabArg(String name) {
        super(name);
    }

    @Override
    public Class getType() {
        return Map.class;
    }

    @Override
    public Object correct(Object value, Workshop ws) throws NoSuchElementException{
        if (value instanceof String) {
            try {
                value = ws.getResource((String) value);
            } catch (NullPointerException e1) {
                try {
                    value = FnJSON.toDoubleMap(new JSONObject(value));
                } catch (JSONException e2) {
                    throw new NoSuchElementException("No such value");
                }
            }
        }
        return value;
    }

    @Override
    public Object parse(String value) throws JSONException {
        return FnJSON.toDoubleMap(new JSONObject(value));
    }
}
