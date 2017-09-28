package mcore;


/**
 *
 * Created by TimeWz on 2017/1/25.
 */
public class Event {
    public static final Event NullEvent = new Event(null, Double.POSITIVE_INFINITY);

    private final Object Value;

    private final double Time;

    public Event(Object val, double time) {
        Value = val;
        Time = time;
    }

    public Object getValue() {
        return Value;
    }

    public double getTime() {
        return Time;
    }

    @Override
    public String toString() {
        try {
            return "Evt(" + Value.toString() + ": " + Time + ")";
        } catch (NullPointerException e) {
            return "NullEvent";
        }
    }
}
