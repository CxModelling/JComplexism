package org.twz.factory.arguments;

import org.twz.factory.Workshop;

import java.util.NoSuchElementException;

/**
 *
 * Created by TimeWz on 2017/11/3.
 */
public class ProbabilityArg extends DoubleArg {
    public ProbabilityArg(String name) {
        super(name);
    }

    @Override
    public Object correct(Object value, Workshop ws) throws NoSuchElementException, AssertionError{
        value = super.correct(value, ws);
        assert (double)value >= 0;
        assert (double)value <= 1;
        return value;
    }

}
