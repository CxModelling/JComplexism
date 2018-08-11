package org.twz.cx.element;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbsScheduler {

    protected String Location;

    protected double GloTime, OwnTime;
    protected List<Request> Requests;
    private List<Disclosure> Disclosures;
    protected Map<ModelAtom, Request> AtomRequests;
    private int NumAtoms;

    public AbsScheduler(String location) {
        Location = location;
        Requests = new ArrayList<>();
        Disclosures = new ArrayList<>();
        AtomRequests = new HashMap<>();
        GloTime = Double.POSITIVE_INFINITY;
        OwnTime = Double.POSITIVE_INFINITY;
        NumAtoms = 0;
    }

    public void addAtom(ModelAtom atom) {
        leaveQueue(atom);
        atom.setScheduler(this);
        NumAtoms += 1;
    }

    public List<Request> getRequests() {
        return Requests;
    }

    public List<Disclosure> getDisclosures() {
        return Disclosures;
    }

    public double getGloTime() {
        return GloTime;
    }

    public void setGloTime(double gloTime) {
        GloTime = gloTime;
    }

    protected abstract void await(ModelAtom atom);

    public void rescheduleAtom(ModelAtom atom) {
        requeue(atom);
        if (atom.getTTE() < OwnTime) putBackCurrentRequests();
    }

    protected abstract void requeue(ModelAtom atom);

    protected abstract void leaveQueue(ModelAtom atom);

    public void removeAtom(ModelAtom atom) {
        atom.dropNext();
        atom.detachScheduler();
        leaveQueue(atom);
        NumAtoms -= 1;
    }

    public void addAndScheduleAtom(ModelAtom atom) {
        addAtom(atom);
        rescheduleAtom(atom);
    }

    public abstract void rescheduleAllAtoms();

    public abstract void rescheduleWaitingAtoms();

    public abstract double getTTE();

    public abstract void extractNext();

    private void putBackCurrentRequests() {
        for (ModelAtom atom : AtomRequests.keySet()) {
            rescheduleAtom(atom);
        }
        AtomRequests = new HashMap<>();
        OwnTime = Double.POSITIVE_INFINITY;
    }

    public int size() {
        return Requests.size() + Disclosures.size();
    }

    public void findNext() {
        rescheduleWaitingAtoms();
        if (AtomRequests.isEmpty()) {
            extractNext();
        }
        Requests = new ArrayList<>(AtomRequests.values());
        if (OwnTime < GloTime) GloTime = OwnTime;
    }

    public void appendDisclosure(Disclosure dis) {
        Disclosures.add(dis);
    }

    public void appendDisclosure(String msg, String who) {
        Disclosures.add(new Disclosure(msg, who, Location));
    }

    public void appendDisclosure(String msg, String who, Map<String, Object> kw) {
        Disclosure dis = new Disclosure(msg, who, Location);
        dis.updateArguments(kw);
        Disclosures.add(dis);
    }

    public List<Request> upScaleRequests(String loc) {
        return Requests.stream().map(req->req.upScale(loc)).collect(Collectors.toList());
    }

    public void appendLowerSchedule(AbsScheduler lower) {
        if (lower.Requests.isEmpty()) return;

        double time = lower.GloTime;
        if (time < GloTime) {
            Requests = lower.upScaleRequests(Location);
            GloTime = time;
        } else if (time == GloTime) {
            Requests.addAll(lower.upScaleRequests(Location));
        }
    }

    public void fetchRequests(List<Request> requests) {
        Requests.stream().filter(req->!requests.contains(req)).forEach(req->req.Todo.cancel());
        Requests = requests;
    }
    
    public Map<String, List<Request>> popLowerRequests() {
        Iterator<Request> it = Requests.iterator();
        Map<String, List<Request>> lower = new HashMap<>();
        while (it.hasNext()) {
            Request req = it.next();
            if (req.reached()) continue;

            try {
                lower.get(req.getGroup()).add(req.downScale().getValue());
            } catch (NullPointerException e) {
                List<Request> temp = new ArrayList<>();
                temp.add(req.downScale().getValue());
                lower.put(req.getGroup(), temp);
            }
            it.remove();
        }
        return lower;
    }

    public boolean isExecutable() {
        return GloTime == OwnTime;
    }

    public List<Disclosure> popDisclosures() {
        List<Disclosure> ds = new ArrayList<>(Disclosures);
        Disclosures.clear();
        return ds;
    }

    public void fetchDisclosures(List<Disclosure> ds) {
        Disclosures.addAll(ds);
    }

    public void toExecutionCompleted() {
        Requests.clear();
        AtomRequests.clear();
        OwnTime = Double.POSITIVE_INFINITY;
    }

    public void toCycleCompleted() {
        Disclosures.clear();
        GloTime = OwnTime;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "Location='" + Location +
                ", Number of Atoms=" + NumAtoms +
                ", Requests=" + Requests.size() +
                ", Disclosures=" + Disclosures.size() +
                ", Time=" + GloTime +
                '}';
    }
}
