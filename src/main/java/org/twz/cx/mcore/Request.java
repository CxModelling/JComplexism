package org.twz.cx.mcore;

import org.apache.commons.math3.util.Pair;


/**
 *
 * Created by TimeWz on 10/02/2017.
 */
public class Request implements Comparable<Request> {
    private String Address;
    private String Node;
    private Event Evt;
    private double Time;

    public Request(Event event, double time, String node, String adr) {
        Address = adr;
        Node = node;
        Evt = event;
        Time = time;
    }

    public Request(Event event, double time, String node) {
        this(event, time, node, "");
    }

    public Request(Event event, double time) {
        this(event, time, "");
    }

    public Request up(String adr) {
        String new_adr;
        if (Address.isEmpty()) {
            new_adr = adr;
        } else {
            new_adr = adr + "@" + Address;
        }

        return new Request(Evt, Time, Node, new_adr);
    }

    Pair<String, Request> down() {
        String[] sp = Address.split("@", 2);
        try {
            return new Pair<>(sp[0], new Request(Evt, Time, Node, sp[1]));
        } catch (IndexOutOfBoundsException e) {
            return new Pair<>(Address, new Request(Evt, Time, Node));
        }

    }

    boolean reached() {
        return !Address.contains("@");
    }

    public String getAddress() {
        return Address;
    }

    public String getNode() {
        return Node;
    }

    public Event getEvent() {
        return Evt;
    }

    public double getTime() {
        return Time;
    }

    @Override
    public String toString() {
        if (Address.isEmpty()) {
            return String.format("_, %s, %s, %.4f", Node, Evt, Time);
        } else {
            return String.format("%s, %s, %s, %.4f", Address, Node, Evt, Time);
        }

    }

    @Override
    public int compareTo(Request o) {
        return Double.compare(this.getTime(), o.getTime());
    }

}
