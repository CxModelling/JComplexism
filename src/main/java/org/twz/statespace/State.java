package org.twz.statespace;

import java.util.List;

/**
 *
 * Created by TimeWz on 2017/1/2.
 */
public class State {
    private final String Name;
    private AbsStateSpace Model;

    public State(String value, AbsStateSpace model) {
        Name = value;
        Model = model;
    }

    public State(String value) {
        Name = value;
    }

    public List<Transition> getNextTransitions() {
        return Model.getTransitions(this);
    }

    public void setModel(AbsStateSpace mod) {
        Model = mod;
    }

    public boolean isa(State s1) {
        return Model.isa(this, s1);
    }

    public String getName() {
        return Name;
    }

    public State exec(Transition tr) {
        return Model.exec(this, tr);
    }

    public String toString() {
        return Name;
    }
}
