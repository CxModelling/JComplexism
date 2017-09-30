package hgm.abmodel;

import java.util.HashMap;
import java.util.Map;
import dcore.State;
import dcore.Transition;
import hgm.abmodel.modifier.ModifierSet;
import mcore.Event;

/**
 * Basic agent for agent based model
 * Created by TimeWz on 2017/6/16.
 */
public class Agent {
    private final String Name;
    private Map<String, Object> Info;
    private State State;
    private Map<Transition, Double> Transitions;
    private ModifierSet Mods;
    private Event Next;

    public Agent(String name, State st) {
        Name = name;
        Info = new HashMap<>();
        State = st;
        Transitions = new HashMap<>();
        Mods = new ModifierSet();
        Next = Event.NullEvent;
    }

    public String getName() {
        return Name;
    }

    public dcore.State getState() {
        return State;
    }

    public Object get(String i) {
        return Info.get(i);
    }

    public void set(String s, Object i) {
        Info.put(s, i);
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


}
