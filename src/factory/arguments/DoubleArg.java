package factory.arguments;

import factory.Workshop;

import java.util.NoSuchElementException;

/**
 *
 * Created by TimeWz on 2017/11/3.
 */
public class DoubleArg extends AbsArgument{
    public DoubleArg(String name) {
        super(name);
    }

    @Override
    public Class getType() {
        return Double.class;
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
}
