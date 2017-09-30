package mcore;

import org.json.JSONString;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public class Clock implements JSONString{
    private double Initial, Last, By;

    public Clock(double initial, double last, double by) {
        Initial = initial;
        Last = last;
        By = by;
    }

    public Clock(double by) {
        this(0, 0, by);
    }

    public void initialise(double ti) {
        Last = Initial;
        update(ti);
    }

    public double getNext() {
        return Last + By;
    }

    public void update(double now) {
        while (now > Last) {
            Last = getNext();
        }
    }

    @Override
    public String toString() {
        return "Clock{" +
                "Initial=" + Initial +
                ", Last=" + Last +
                ", By=" + By +
                '}';
    }

    @Override
    public String toJSONString() {
        return "{" +
                "t0: " + Initial +
                ", t: " + Last +
                ", dt: " + By +
                '}';
    }
}
