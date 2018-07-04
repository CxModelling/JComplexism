package org.twz.cx.element;

import org.twz.dataframe.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Schedule implements Comparable<Schedule>  {
    private final String Location;
    private List<Request> Requests;
    private List<Disclosure> Disclosures;
    private double Time;
    private Status CurrentStatus;

    public Schedule(String loc) {
        Location = loc;
        Requests = new ArrayList<>();
        Disclosures = new ArrayList<>();
        Time = Double.POSITIVE_INFINITY;
        CurrentStatus = Status.ToCollect;
    }

    public int size() {
        return Requests.size() + Disclosures.size();
    }

    public boolean isEmpty() {
        return Requests.isEmpty() & Disclosures.isEmpty();
    }

    public List<Request> getRequests() {
        return Requests;
    }

    public List<Disclosure> getDisclosures() {
        return Disclosures;
    }

    public boolean appendRequest(Request req) {
        double ti = req.getTime();
        if (ti < Time) {
            Requests.clear();
            Requests.add(req);
            Time = ti;
            return true;
        } else if (ti == Time) {
            Requests.add(req);
            return true;
        } else {
            return false;
        }
    }

    public boolean appendRequestFromSource(Event event, String who) {
        if (event.getTime() <= Time) {
            return appendRequest(new Request(event, who, Location));
        } else {
            return false;
        }
    }

    public void appendDisclosure(Disclosure dis) {
        Disclosures.add(dis);
    }

    public void appendDisclosureFromSource(String msg, String who) {
        Disclosure dis = new Disclosure(msg, who, Location);
        appendDisclosure(dis);
    }

    public void appendDisclosureFromSource(String msg, String who, Map<String, Object> args) {
        Disclosure dis = new Disclosure(msg, who, Location);
        dis.updatArguments(args);
        appendDisclosure(dis);
    }

    public List<Request> upScaleRequests(String adr) {
        return Requests.stream().map(r -> r.upScale(adr)).collect(Collectors.toList());
    }

    public boolean appendLowerSchedule(Schedule lower) {
        double ti = lower.Time;
        if (ti < Time) {
            if (!lower.Requests.isEmpty()) {
                Requests.clear();
                Requests.addAll(lower.upScaleRequests(Location));
                Time = ti;
            }
            return true;
        } else if (ti == Time) {
            Requests.addAll(lower.upScaleRequests(Location));
            return true;
        } else {
            return false;
        }
    }

    public void fetchRequest(List<Request> rs) {
        Requests.clear();
        Requests.addAll(rs);
    }

    public Map<String, List<Request>> popLowerRequests() {
        List<Request> lowers = Requests.stream().filter(r -> !r.reached()).collect(Collectors.toList());
        Requests.removeAll(lowers);
        Map<String, List<Request>> pop = new HashMap<>();

        for (Request r: lowers) {
            Pair<String, Request> lo = r.downScale();
            try {
                pop.get(lo.getKey()).add(lo.getValue());
            } catch (NullPointerException e) {
                List<Request> rl = new ArrayList<>();
                rl.add(lo.getValue());
                pop.put(lo.getKey(), rl);
            }
        }

        if (Requests.isEmpty() & lowers.isEmpty()) {
            executionCompleted();
        }

        return pop;
    }

    public List<Disclosure> popDisclosures() {
        List<Disclosure> ds = new ArrayList<>(Disclosures);
        Disclosures.clear();
        return ds;
    }

    public void fetchDisclosures(List<Disclosure> ds) {
        Disclosures.addAll(ds);
    }


    public boolean isWaitingForCollection() {
        return CurrentStatus == Status.ToCollect;
    }

    public boolean isWaitingForValidation() {
        return CurrentStatus == Status.ToValidate;
    }

    public boolean isWaitingForExecution() {
        return CurrentStatus == Status.ToExecute;
    }

    public boolean isWaitingForFinish() {
        return CurrentStatus == Status.ToFinish;
    }

    public void collectionCompleted() {
        CurrentStatus = Status.ToValidate;
    }

    public void validationCompleted() {
        if (isEmpty()) {
            executionCompleted();
        } else {
            CurrentStatus = Status.ToExecute;
        }
    }

    public void executionCompleted() {
        CurrentStatus = Status.ToFinish;
        Requests.clear();
        Time = Double.POSITIVE_INFINITY;
    }

    public void cycleCompleted() {
        CurrentStatus = Status.ToCollect;
        Disclosures.clear();
        Requests.clear();
        Time = Double.POSITIVE_INFINITY;
    }

    public void cycleBroken() {
        cycleCompleted();
        collectionCompleted();
    }

    @Override
    public int compareTo(Schedule o) {
        return Double.compare(this.Time, o.Time);
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "Location='" + Location +
                ", Requests=" + Requests.size() +
                ", Disclosures=" + Disclosures.size() +
                ", Time=" + Time +
                ", Status=" + CurrentStatus +
                '}';
    }
}
