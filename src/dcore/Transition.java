package dcore;


import pcore.distribution.IDistribution;
import net.sourceforge.jdistlib.rng.RandomEngine;

/**
 *
 * Created by TimeWz on 2017/1/2.
 */
public class Transition {
    private final String Name;
    private final State State;
    private final IDistribution Dist;

    public Transition(String name, State state, IDistribution dist) {
        Name = name;
        State = state;
        Dist = dist;
    }

    public double rand() {
        return Dist.sample();
    }

    public State getState() {
        return State;
    }

    public void setSeed(RandomEngine rng) {
        Dist.setRandomEngine(rng);
    }

    public String getName() {
        return Name;
    }

    @Override
    public String toString() {
        return Name + "(" + State + ", " + Dist.toString() + ")";
    }
}
