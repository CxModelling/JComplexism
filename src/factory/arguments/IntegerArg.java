package factory.arguments;

import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import factory.Workshop;

import java.util.NoSuchElementException;

/**
 *
 * Created by TimeWz on 2017/11/3.
 */
public class IntegerArg extends AbsArgument{
    public IntegerArg(String name) {
        super(name);
    }

    @Override
    public Class getType() {
        return Integer.class;
    }

    @Override
    public Object correct(Object value, Workshop ws) throws NoSuchElementException{
        if (value instanceof String) {
            try {
                value = ws.getResource((String) value);
            } catch (NullPointerException e) {
                throw new NoSuchElementException("No such value");
            }
        }
        return value;
    }

    @Override
    public Object parse(String value) {
        return Integer.parseInt(value);
    }
}
