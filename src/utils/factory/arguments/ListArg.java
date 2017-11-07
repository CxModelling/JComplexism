package utils.factory.arguments;

import utils.factory.Workshop;
import utils.json.FnJSON;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.NoSuchElementException;

/**
 *
 * Created by TimeWz on 2017/11/3.
 */
public class ListArg extends AbsArgument{

    private String Type;
    public ListArg(String name, String type) {
        super(name);
        Type = type;
    }

    public ListArg(String name) {
        this(name, "Double");
    }

    @Override
    public Class getType() {
        switch (Type) {
            case "Double":
                return List.class;
            case "String":
                return List.class;
            default:
                return List.class;
        }
    }

    @Override
    public Object correct(Object value, Workshop ws) throws NoSuchElementException{
        if (value instanceof String) {
            try {
                value = ws.getResource((String) value);
            } catch (NullPointerException e1) {
                try {
                    value = new JSONArray(value);
                } catch (JSONException e2) {
                    throw new NoSuchElementException("No such value");
                }
            }
        }
        if (value instanceof JSONArray) {
            switch (Type) {
                case "Double":
                    value = FnJSON.toDoubleList((JSONArray) value);
                    break;
                case "String":
                    value = FnJSON.toStringList((JSONArray) value);
                    break;
                default:
                    value = FnJSON.toStringList((JSONArray) value);
                    break;
            }
        }

        return value;
    }

    @Override
    public Object parse(String value) {
        return FnJSON.toObjectList(new JSONArray(value));
    }
}
