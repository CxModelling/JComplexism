package org.twz.cx.element;

import java.util.*;

public class ArrayListScheduler extends AbsScheduler {
    private List<ModelAtom> Atoms;
    private Set<ModelAtom> Waiting;

    public ArrayListScheduler(String location) {
        super(location);
        Atoms = new ArrayList<>();
        Waiting = new HashSet<>();
    }

    @Override
    protected void joinScheduler(ModelAtom atom) {
        Atoms.add(atom);
        Waiting.add(atom);
    }

    @Override
    protected void leaveScheduler(ModelAtom atom) {
        Atoms.remove(atom);
        Waiting.remove(atom);
        popFromComing(atom);
    }

    @Override
    protected void await(ModelAtom atom) {
        Waiting.add(atom);
        popFromComing(atom);
    }

    @Override
    public void rescheduleAllAtoms() {
        Coming.clear();
        OwnTime = Double.POSITIVE_INFINITY;
        rescheduleSet(Atoms);
        Waiting.clear();
    }

    @Override
    public void rescheduleWaitingAtoms() {
        if (Coming.isEmpty()) {
            rescheduleAllAtoms();
        } else {
            rescheduleSet(Waiting);
            Waiting.clear();
        }
    }

    @Override
    public void findComingAtoms() {
        if (Coming.isEmpty()) {
            rescheduleAllAtoms();
        }
    }

    private void rescheduleSet(Collection<ModelAtom> atoms) {
        for (ModelAtom atom: atoms) {
            Event event = atom.getNext();
            if (event.getTime() < OwnTime) {
                Coming.clear();
                Coming.add(atom);
                OwnTime = event.getTime();
            } else if (event.getTime() == OwnTime) {
                Coming.add(atom);
            }
        }
        CountRequeuing += atoms.size();
    }
}
