package org.twz.factory.arguments;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.factory.Workshop;
import org.twz.io.FnJSON;

import java.util.NoSuchElementException;

public class JSONObjectArg extends AbsArgument{
    public JSONObjectArg(String name) {
        super(name);
    }

    @Override
    public Class getType() {
        return JSONObject.class;
    }

    @Override
    public Object correct(Object value, Workshop ws) throws NoSuchElementException {
        if (value instanceof String) {
            return new JSONObject(value);
        }
        return value;
    }

    @Override
    public Object parse(String value) throws JSONException {
        return new JSONObject(value);
    }
}