package org.twz.cx.abmodel;

import org.json.JSONObject;
import org.twz.cx.abmodel.modifier.AbsModifier;
import org.twz.cx.abmodel.modifier.ModifierSet;
import org.twz.cx.element.Event;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.dag.Gene;
import org.twz.statespace.AbsDCore;
import org.twz.statespace.State;
import org.twz.statespace.Transition;

import java.util.*;
import java.util.stream.Collectors;

public class StSpAgent extends AbsAgent {
    private State State;
    private Map<Transition, Double> Transitions;
    private ModifierSet Mods;

    public StSpAgent(String name, Gene pars, org.twz.statespace.State state) {
        super(name, pars);
        State = state;
        Transitions = new HashMap<>();
        Mods = new ModifierSet();
    }

    public org.twz.statespace.State getState() {
        return State;
    }

    @Override
    public Object get(String key) {
        if (key.equals("State")) {
            return State;
        } else {
            return super.get(key);
        }
    }

    @Override
    protected Event findNext() {
        if (Transitions.isEmpty()) {
            return Event.NullEvent;
        }
        Transition tr = null;
        double time = Double.POSITIVE_INFINITY;

        for (Map.Entry<Transition, Double> ent: Transitions.entrySet()) {
            if (ent.getValue() < time) {
                tr = ent.getKey();
                time = ent.getValue();
            }
        }
        return new Event(tr, time);
    }

    @Override
    public void updateTo(double ti) {
        Transitions = Transitions.entrySet().stream()
                .filter(e-> e.getValue() > ti)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        List<Transition> new_trs = State.getNextTransitions();
        Set<Transition> add = new HashSet<>(new_trs);

        add.removeAll(Transitions.keySet());

        Transitions = Transitions.entrySet().stream()
                .filter(e-> new_trs.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        double tte;
        for (Transition tr: add) {
            tte = tr.rand();
            for (AbsModifier mod: Mods.on(tr)) {
                tte = mod.modify(tte);
            }
            Transitions.put(tr, tte + ti);
        }
        dropNext();
    }

    @Override
    public void executeEvent() {
        Event next = getNext();
        if (!next.isCancelled()) {
            State = State.exec((Transition) next.getValue());
        }
    }

    @Override
    public void initialise(double ti, AbsSimModel model) {
        Transitions.clear();
        updateTo(ti);
    }

    @Override
    public void reset(double ti, AbsSimModel model) {
        Transitions.clear();
        updateTo(ti);
    }

    @Override
    public void shock(double ti, Object source, String target, Object value) {
        AbsModifier mod = Mods.get(target);
        if (mod.update(value)) {
            modify(target, ti);
        }
    }

    public void addMod(AbsModifier mod) {
        Mods.put(mod.getName(), mod);
    }


    public void modify(String m, double ti) {
        AbsModifier mod = Mods.get(m);
        Transition tr = mod.getTarget();
        if (Transitions.containsKey(tr)) {
            double tte = tr.rand();
            tte = Mods.on(tr).stream().reduce(tte, (sum, p) -> p.modify(sum), (sum1, sum2) -> sum2);
            Transitions.put(tr, tte + ti);
            dropNext();
        }
    }

    public boolean isa(State st) {
        return State.isa(st);
    }

    public StSpAgent deepcopy(AbsDCore dc_new, List<String> tr_ch) {
        StSpAgent ag = new StSpAgent(getName(), Parameters, dc_new.getState(State.getName()));
        for (Map.Entry<Transition, Double> ent: Transitions.entrySet()) {
            if (!tr_ch.contains(ent.getKey().getName())) {
                ag.Transitions.put(dc_new.getTransition(ent.getKey().getName()), ent.getValue());
            }
        }
        return ag;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }
}
