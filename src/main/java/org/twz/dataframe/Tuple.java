package org.twz.dataframe;

/**
 *
 * Created by TimeWz on 2017/7/16.
 */
public class Tuple <T1, T2, T3> {
    private final T1 First;
    private final T2 Second;
    private final T3 Third;

    public Tuple(T1 first, T2 second, T3 third) {
        First = first;
        Second = second;
        Third = third;
    }

    public T1 getFirst() {
        return First;
    }

    public T2 getSecond() {
        return Second;
    }

    public T3 getThird() {
        return Third;
    }

    public String toString() {
        return "("+First+", "+Second+", "+Third+")";
    }
}
