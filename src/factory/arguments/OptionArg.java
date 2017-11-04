package factory.arguments;

import factory.Workshop;
import hgm.utils.FnJSON;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * Created by TimeWz on 2017/11/3.
 */
public class OptionArg extends AbsArgument{
    private String Source;
    private Class Cls;

    public OptionArg(String name, String src, Class cls) {
        super(name);
        Source = src;
        Cls = cls;
    }

    @Override
    public Class getType() {
        return Cls;
    }

    @Override
    public Object correct(Object value, Workshop ws) throws NoSuchElementException{
        if (Cls.isInstance(value)) {
            return value;
        } else {
            try {
                return ((Map) ws.getResource((String) value)).get(value);
            } catch (NullPointerException e1) {
                throw new NoSuchElementException("No such value");
            }
        }
    }
}
