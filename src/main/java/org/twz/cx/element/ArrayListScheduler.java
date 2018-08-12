package org.twz.cx.element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArrayListScheduler extends AbsScheduler {
    private List<ModelAtom> Atoms;
    private Set<ModelAtom> Waiting;

    public ArrayListScheduler(String location) {
        super(location);
        Atoms = new ArrayList<>();
        Waiting = new HashSet<>();
    }

    @Override
    protected void join(ModelAtom atom) {
        Atoms.add(atom);
        Waiting.add(atom);
    }

    @Override
    protected void await(ModelAtom atom) {
        Waiting.add(atom);
    }

    @Override
    protected void requeue(ModelAtom atom) {
        Event event = atom.getNext();
        double time = event.getTime();
        if (time < OwnTime) {
            AtomRequests.clear();
            AtomRequests.put(atom, new Request(event, atom.getName(), Location));
        } else if (time == OwnTime) {
            AtomRequests.put(atom, new Request(event, atom.getName(), Location));
        }
        Waiting.remove(atom);
    }

    @Override
    protected void leaveQueue(ModelAtom atom) {
        AtomRequests.remove(atom);
        if (AtomRequests.isEmpty()) OwnTime = Double.POSITIVE_INFINITY;
        Atoms.remove(atom);
        Waiting.remove(atom);
    }

    @Override
    public void rescheduleAllAtoms() {
        AtomRequests.clear();
        
    }

    @Override
    public void rescheduleWaitingAtoms() {

    }

    @Override
    public void extractNext() {

    }
}
