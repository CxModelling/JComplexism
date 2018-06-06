package org.twz.dataframe;

/**
 *
 * Created by TimeWz on 2017/7/16.
 */
public class Pair<T1, T2> {
    private final T1 First;
    private final T2 Second;

    public Pair(T1 first, T2 second) {
        First = first;
        Second = second;
    }

    public T1 getFirst() {
        return First;
    }

    public T2 getSecond() {
        return Second;
    }

    public T1 getKey() {
        return First;
    }

    public T2 getValue() {
        return Second;
    }

    public String toString() {
        return "("+First+", "+Second+")";
    }
}
