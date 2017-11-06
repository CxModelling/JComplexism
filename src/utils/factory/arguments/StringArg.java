package utils.factory.arguments;

import utils.factory.Workshop;

import java.util.NoSuchElementException;

/**
 *
 * Created by TimeWz on 2017/11/3.
 */
public class StringArg extends AbsArgument{
    public StringArg(String name) {
        super(name);
    }

    @Override
    public Class getType() {
        return String.class;
    }

    @Override
    public Object correct(Object value, Workshop ws) throws NoSuchElementException{
        return value;
    }

    @Override
    public Object parse(String value) {
        return value;
    }
}
