package mcore.Ticker;

import org.json.JSONObject;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/10/13.
 */
public class AppointmentTicker extends AbsTicker {
    private LinkedList<Double> Queue;

    public AppointmentTicker() {
        super();
        Queue = new LinkedList<>();
    }

    public void makeAnAppointment(double t) {
        if (t > Last) Queue.addLast(t);
    }

    @Override
    public void update(double now) {
        super.update(now);
        if (Queue.isEmpty()) return;
        while(Queue.getFirst() < now) Queue.removeFirst();
    }

    @Override
    public double getNext() {
        for (double t: Queue) {
            if (t > Last)
                return t;
        }
        return Double.POSITIVE_INFINITY;
    }

    @Override
    JSONObject getArguments() {
        JSONObject js = new JSONObject();
        js.put("queue", Queue);
        return js;
    }


}
