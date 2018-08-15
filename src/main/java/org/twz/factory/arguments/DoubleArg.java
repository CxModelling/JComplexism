package org.twz.factory.arguments;

import org.twz.factory.Workshop;

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
    public Object correct(Object value, Workshop ws) throws NoSuchElementException, AssertionError{
        if (value instanceof String) {
            try {
                value = ws.getResource((String) value);
            } catch (NullPointerException e) {
                throw new NoSuchElementException("No such value");
            }
        }
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        return value;
    }

    @Override
    public Object parse(String value) {
        return Double.parseDouble(value);
    }
}
