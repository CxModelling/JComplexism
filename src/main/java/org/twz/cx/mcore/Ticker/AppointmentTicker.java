package org.twz.cx.mcore.Ticker;

import org.json.JSONObject;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/10/13.
 */
public class AppointmentTicker extends AbsTicker {
    private LinkedList<Double> Queue;

    public AppointmentTicker(String name,List<Double> queue) {
        super(name);
        Queue = new LinkedList<>();
        Queue.addAll(queue);
    }

    public AppointmentTicker(String name, List<Double> queue, Double t) {
        this(name, queue);
        initialise(t);
    }

    public void makeAnAppointment(double t) {
        if (t > Last) Queue.addLast(t);
    }

    @Override
    public void update(double now) {
        super.update(now);
        if (Queue.isEmpty()) return;
        while (Queue.getFirst() < now) Queue.removeFirst();
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

    @Override
    String getType() {
        return "Appointment";
    }


}
