package mcore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 10/02/2017.
 */
public class RequestSet {
    private List<Request> Requests;

    private double Time;

    public RequestSet() {
        Time = Double.POSITIVE_INFINITY;
        Requests = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "#Requests=" + Requests.size() + ", Time=" + Time;
    }

    public double getTime() {
        return Time;
    }

    public List<Request> up(String adr) {
        return Requests.stream()
                .map(e-> e.up(adr))
                .collect(Collectors.toList());
    }

    public List<Request> popLowerRequests() {
        List<Request> lower = Requests.stream()
                .filter(e -> !e.reached()).collect(Collectors.toList());
        Requests.removeAll(lower);

        if (Requests.isEmpty()) Time = Double.POSITIVE_INFINITY;
        return lower;
    }

    public void clear() {
        Requests.clear();
        Time = Double.POSITIVE_INFINITY;
    }

    public int size() {
        return Requests.size();
    }

    public void append(Request req) {
        if (req.getTime() < Time) {
            Requests.clear();
            Requests.add(req);
            Time = req.getTime();
        } else if (req.getTime() == Time) {
            Requests.add(req);
        }
    }

    public void appendSRC(String adr, String ag, Event evt, double ti) {
        if (ti < Time) {
            Requests.clear();
            Requests.add(new Request(adr, ag, evt, ti));
            Time = ti;
        } else if (ti == Time) {
            Requests.add(new Request(adr, ag, evt, ti));
        }
    }

    public void add(List<Request> requests) {
        double ti = requests.get(0).getTime();
        if (ti < Time) {
            Requests.clear();
            Requests.addAll(requests);
            Time = ti;
        } else {
            Requests.addAll(requests);
        }
    }

    public List<Request> getRequests() {
        return Requests;
    }

    public boolean isEmpty() {
        return Requests.isEmpty();
    }

    public void merge(RequestSet rqs, String ag) {
        double ti = rqs.getTime();
        if (ti < Time) {
            Requests.clear();
            Requests.addAll(rqs.Requests);
            Time = ti;
        } else {
            Requests.addAll(rqs.Requests);
        }
    }

}
