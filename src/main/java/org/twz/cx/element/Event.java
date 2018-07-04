package org.twz.cx.element;


/**
 *
 * Created by TimeWz on 2017/1/25.
 */
public class Event {
    public static final Event NullEvent = new Event("Null", Double.POSITIVE_INFINITY);

    private final Object Value;
    private final String Message;
    private final double Time;

    public Event(Object val, String msg, double time) {
        Value = val;
        Message = msg;
        Time = time;
    }

    public Event(Object val, double time) {
        this(val, val.toString(), time);
    }

    public Object getValue() {
        return Value;
    }

    public String getMessage() {
        return Message;
    }

    public double getTime() {
        return Time;
    }

    @Override
    public String toString() {
        return "Evt(" + this.getMessage() + ": " + Time + ")";
    }
}
