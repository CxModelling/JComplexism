package org.twz.cx.element;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class PriorityQueueScheduler extends AbsScheduler {
    private Queue<ModelAtom> AtomQueue;
    private Set<ModelAtom> AtomWaiting;

    public PriorityQueueScheduler(String location) {
        super(location);
        AtomQueue = new PriorityQueue<>();
        AtomWaiting = new HashSet<>();
    }

    @Override
    protected void joinScheduler(ModelAtom atom) {
        AtomWaiting.add(atom);
    }

    @Override
    protected void leaveScheduler(ModelAtom atom) {
        AtomQueue.remove(atom);
        AtomWaiting.remove(atom);
    }

    @Override
    protected void await(ModelAtom atom) {
        AtomQueue.remove(atom);
        AtomWaiting.add(atom);
        popFromComing(atom);
    }

    @Override
    public void rescheduleAllAtoms() {
        Coming.clear();
        OwnTime = Double.POSITIVE_INFINITY;
        AtomQueue = new PriorityQueue<>(AtomQueue);
        AtomQueue.addAll(AtomWaiting);
        AtomWaiting.clear();
    }

    @Override
    public void rescheduleWaitingAtoms() {
        AtomQueue.addAll(AtomWaiting);
        AtomWaiting.clear();
    }

    @Override
    public void findComingAtoms() {
        OwnTime = AtomQueue.peek().getTTE();
        if (Double.isInfinite(OwnTime)) return;

        Coming.clear();
        while (!AtomQueue.isEmpty()) {
            double tte = AtomQueue.peek().getTTE();
            if (tte == OwnTime) {
                ModelAtom atom = AtomQueue.poll();
                Event event = atom.getNext();
                Coming.add(atom);
                OwnTime = event.getTime();
                //AtomQueue.remove(atom);
                AtomWaiting.add(atom);
            } else if (tte > OwnTime) {
                System.out.println("Error"); // todo
                return;
            }
        }
    }
}
