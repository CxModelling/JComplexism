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
    protected void await(ModelAtom atom) {
        AtomWaiting.add(atom);
    }

    @Override
    protected void leaveQueue(ModelAtom atom) {
        AtomQueue.remove(atom);
        AtomWaiting.remove(atom);
        AtomRequests.remove(atom);
    }

    @Override
    protected void requeue(ModelAtom atom) {
        AtomQueue.add(atom);
        AtomWaiting.remove(atom);
    }

    @Override
    public void rescheduleAllAtoms() {
        AtomQueue = new PriorityQueue<>(AtomQueue);
        AtomQueue.addAll(AtomWaiting);
    }

    @Override
    public void rescheduleWaitingAtoms() {
        AtomWaiting.forEach(this::rescheduleAtom);
        AtomWaiting.clear();
    }

    @Override
    public double getTTE() {
        return AtomQueue.peek().getTTE();
    }

    @Override
    public void extractNext() {
        while (!AtomQueue.isEmpty()) {
            if (AtomQueue.peek().getTTE() <= OwnTime) {
                ModelAtom atom = AtomQueue.remove();
                Event event = atom.getNext();
                AtomRequests.put(atom, new Request(event, atom.getName(), Location));
                OwnTime = event.getTime();
                await(atom);
            } else {
                return;
            }
        }
    }
}
