package hgm.abmodel;

import java.util.*;
import java.util.stream.Collectors;

import dcore.AbsDCore;
import dcore.State;
import dcore.Transition;
import hgm.abmodel.modifier.AbsModifier;
import hgm.abmodel.modifier.ModifierSet;
import utils.json.AdapterJSONObject;
import mcore.Event;
import org.json.JSONObject;

/**
 * Basic agent for agent based model
 * Created by TimeWz on 2017/6/16.
 */
public class Agent implements AdapterJSONObject{
    private final String Name;
    private Map<String, Object> Info;
    private State State;
    private Map<Transition, Double> Transitions;
    private ModifierSet Mods;
    private Event Next;

    public Agent(String name, State st, Map<String, Object> info) {
        Name = name;
        Info = info;
        State = st;
        Transitions = new HashMap<>();
        Mods = new ModifierSet();
        Next = Event.NullEvent;
    }

    public Agent(String name, State st) {
        this(name, st, new HashMap<>());
    }

    public String getName() {
        return Name;
    }

    public dcore.State getState() {
        return State;
    }


    public void updateInfo(Map<String, Object> info, boolean force) {
        if (force) {
            Info.putAll(info);
        } else {
            for (Map.Entry<String, Object> ent: info.entrySet()) {
                Info.putIfAbsent(ent.getKey(), ent.getValue());
            }
        }
    }

    public boolean hasInfo(String s) {
        return Info.containsKey(s);
    }

    public Map<String, Object> getInfo() {
        return Info;
    }

    public Object getInfo(String i) {
        return Info.get(i);
    }

    public void setInfo(String s, Object o) {
        Info.put(s, o);
    }

    public Event next() {
        if (Next == Event.NullEvent) {
            Next = findNext();
        }
        return Next;
    }

    public double tte() {
        return next().getTime();
    }

    private Event findNext() {
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

    public void dropNext() {
        Next = Event.NullEvent;
    }

    public void addMod(AbsModifier mod) {
        Mods.put(mod.getName(), mod);
    }

    public void initialise(double time) {
        Transitions.clear();
        update(time);
        dropNext();
    }

    public void assign(Event evt) {
        Next = evt;
    }

    public void exec(Event evt) {
        if (evt != Event.NullEvent) {
            State = State.exec((Transition) evt.getValue());
        }
    }

    public void update(double ti) {
        List<Transition> new_trs = State.getNextTransitions();
        Set<Transition> add = new HashSet<>();
        add.addAll(new_trs);
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

    public AbsModifier getModifier(String m) {
        return Mods.get(m);
    }

    public void shock(String m, double val, double ti) {
        AbsModifier mod = Mods.get(m);
        Transition tr = mod.getTarget();
        if (mod.update(val) & Transitions.containsKey(tr)) {
            double tte = tr.rand();
            tte = Mods.on(tr).stream().reduce(tte, (sum, p) -> p.modify(sum), (sum1, sum2)-> sum2);
            Transitions.put(tr, tte + ti);
            dropNext();
        }
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

    @Override
    public String toString() {
        if (Info.isEmpty()) return "Agent{Name='" + Name + ", State=" + State + '}';
        return "Agent{" +
                "Name='" + Name + '\'' +
                ", " + Info.entrySet().stream()
                .map(e-> e.getKey()+": "+e.getValue())
                .collect(Collectors.joining(", ")) +
                ", St=" + State +
                '}';
    }

    public Agent deepcopy(AbsDCore dc_new, List<String> tr_ch) {
        Agent ag = new Agent(Name, dc_new.getState(State.getName()));
        for (Map.Entry<Transition, Double> ent: Transitions.entrySet()) {
            if (!tr_ch.contains(ent.getKey().getName())) {
                ag.Transitions.put(dc_new.getTransition(ent.getKey().getName()), ent.getValue());
            }
        }
        return ag;
    }

    public Agent deepcopy(AbsDCore dc_new) {
        return deepcopy(dc_new, new ArrayList<>());
    }

    public Agent deepcopy() {
        Agent ag = new Agent(Name, State);
        ag.updateInfo(Info, true);
        return ag;
    }

    @Override
    public JSONObject toJSON() {
        //todo
        return null;
    }

    public JSONObject toSnapshot() {
        //todo
        return null;
    }
}
