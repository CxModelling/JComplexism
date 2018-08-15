package org.twz.factory.arguments;

import org.twz.factory.Workshop;

import java.util.NoSuchElementException;

/**
 *
 * Created by TimeWz on 2017/11/3.
 */
public abstract class AbsArgument {
    private final String Name;

    public AbsArgument(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public abstract Class getType();

    public abstract Object correct(Object value, Workshop ws) throws NoSuchElementException, AssertionError;

    public abstract Object parse(String value);

    public String toString() {
        return Name + "(" + getType().getSimpleName() + ")";
    }
}
