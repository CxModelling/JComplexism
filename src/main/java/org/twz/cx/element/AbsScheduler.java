package org.twz.cx.element;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbsScheduler {
    public static String DefaultScheduler = "PriorityQueue";

    public static AbsScheduler getScheduler(String name) {
        switch (DefaultScheduler) {
            case "PriorityQueue":
                return new PriorityQueueScheduler(name);
            case "ArrayList":
                return new ArrayListScheduler(name);
        }
        return new ArrayListScheduler(name);
    }



    protected String Location;

    protected double OwnTime;
    private double GloTime;

    protected Set<ModelAtom> Coming;
    private Map<ModelAtom, Request> AtomRequests;
    private List<Request> Requests;
    private List<Disclosure> Disclosures;

    private int NumAtoms;

    protected long CountRequests, CountDisclosure, CountRequeuing;

    public AbsScheduler(String location) {
        Location = location;
        Requests = new ArrayList<>();
        Disclosures = new ArrayList<>();
        AtomRequests = new HashMap<>();
        Coming = new HashSet<>();

        GloTime = Double.POSITIVE_INFINITY;
        OwnTime = Double.POSITIVE_INFINITY;
        NumAtoms = 0;

        CountDisclosure = 0;
        CountRequests = 0;
        CountRequeuing = 0;
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

    public void addAtom(ModelAtom atom) {
        joinScheduler(atom);
        atom.setScheduler(this);
        NumAtoms += 1;
    }

    protected abstract void joinScheduler(ModelAtom atom);

    protected abstract void leaveScheduler(ModelAtom atom);

    public void removeAtom(ModelAtom atom) {
        atom.dropNext();
        atom.detachScheduler();
        leaveScheduler(atom);
        popFromComing(atom);

        NumAtoms -= 1;
    }

    protected abstract void await(ModelAtom atom);

    public void addAndScheduleAtom(ModelAtom atom) {
        addAtom(atom);
        await(atom);
    }

    public abstract void rescheduleAllAtoms();

    public abstract void rescheduleWaitingAtoms();

    public abstract void findComingAtoms();

    public void extractCurrentRequests() {
        for (ModelAtom atom : Coming) {
            Event event = atom.getNext();
            AtomRequests.put(atom, new Request(event, atom.getName(), Location));
        }
    }

    protected void popFromComing(ModelAtom atom) {
        Coming.remove(atom);
        if (Coming.isEmpty()) OwnTime = Double.POSITIVE_INFINITY;
    }

    private void checkCurrentRequests() {
        if (NumAtoms == 0) return;

        if (Coming.isEmpty() || !Coming.containsAll(AtomRequests.keySet())) {
            findComingAtoms();
            AtomRequests.clear();
        }

        if (AtomRequests.isEmpty()) {
            extractCurrentRequests();
        }
    }

    public void findNext() {
        rescheduleWaitingAtoms();
        checkCurrentRequests();
        Requests = new ArrayList<>(AtomRequests.values());
        if (OwnTime < GloTime) {
            GloTime = OwnTime;
        }
    }

    public boolean isExecutable() {
        return GloTime == OwnTime;
    }

    private List<Request> upScaleRequests(String loc) {
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
        Requests = requests;
        CountRequests += requests.size();
    }

    public Map<String, List<Request>> popLowerRequests() {
        Iterator<Request> it = Requests.iterator();
        Map<String, List<Request>> lower = new HashMap<>();
        while (it.hasNext()) {
            Request req = it.next();
            if (req.reached()) continue;

            try {
                req = req.downScale().getValue();
                lower.get(req.getGroup()).add(req);
            } catch (NullPointerException e) {
                List<Request> temp = new ArrayList<>();
                temp.add(req.downScale().getValue());
                lower.put(req.getGroup(), temp);
            }
            it.remove();
        }
        return lower;
    }

    public int size() {
        return Requests.size() + Disclosures.size();
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

    public List<Disclosure> popDisclosures() {
        List<Disclosure> ds = new ArrayList<>(Disclosures);
        Disclosures.clear();
        return ds;
    }

    public void fetchDisclosures(List<Disclosure> ds) {
        Disclosures.addAll(ds);
        CountDisclosure += ds.size();
    }

    public void toExecutionCompleted() {
        Requests.clear();
        AtomRequests.clear();
        Coming.clear();
        OwnTime = Double.POSITIVE_INFINITY;
    }

    public void toCycleCompleted() {
        Requests.clear();
        Disclosures.clear();
        GloTime = Double.POSITIVE_INFINITY;
    }

    public void printEventCounts() {
        System.out.println(String.format("Counts: Requests %d, Discloses %d, Requeuing %d", CountRequests, CountDisclosure, CountRequeuing));
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
