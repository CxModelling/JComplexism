package mcore;

import org.apache.commons.math3.util.Pair;


/**
 *
 * Created by TimeWz on 10/02/2017.
 */
public class Request implements Comparable<Request> {
    public final static Request NullRequest =
            new Request("", "", null, Double.POSITIVE_INFINITY);

    private String Address;
    private String Node;
    private Event Evt;
    private double Time;

    public Request(String address, String node, Event event, double time) {
        Address = address;
        Node = node;
        Evt = event;
        Time = time;
    }

    public Request up(String adr) {
        String new_adr;
        if (Address.isEmpty()) {
            new_adr = adr;
        } else {
            new_adr = adr + "@" + Address;
        }

        return new Request(new_adr, Node, Evt, Time);
    }

    Pair<String, Request> down() {
        String[] sp = Address.split("@", 2);

        return new Pair<>(sp[0], new Request(sp[1], Node, Evt, Time));
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
        return String.format("%s, %s, %s, %.4f", Address, Node, Evt, Time);
    }

    @Override
    public int compareTo(Request o) {
        return Double.compare(this.getTime(), o.getTime());
    }

}
