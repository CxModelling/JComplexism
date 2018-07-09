package org.twz.cx.mcore;

import org.twz.cx.element.Event;
import org.twz.io.AdapterJSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ModelAtom implements AdapterJSONObject {
    private final String Name;
    protected Map<String, Object> Parameters, Attributes;
    protected Event Next;

    public ModelAtom(String name, Map<String, Object> parameters) {
        Name = name;
        Parameters = parameters;
        Attributes = new HashMap<>();
        Next = Event.NullEvent;
    }

    public ModelAtom(String name) {
        this(name, null);
    }

    public String getName() {
        return Name;
    }

    public Object get(String key) {
        try {
            return Attributes.get(key);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void put(String key, Object value) {
        Attributes.put(key, value);
    }

    public Event getNext() {
        if (Next == Event.NullEvent) {
            Next = findNext();
        }
        return Next;
    }

    public double getTTE() {
        return Next.getTime();
    }

    protected abstract Event findNext();

    public void dropNext() {
        Next = Event.NullEvent;
    }

    public void approveEvent(Event evt) {
        Next = evt;
    }

    public void disapproveEvent(double ti) {
        updateTo(ti);
    }

    protected abstract void updateTo(double ti);

    public abstract void executeEvent();

    public abstract void initialise(double ti, AbsSimModel model);

    public abstract void reset(double ti, AbsSimModel model);

    public abstract void shock(double ti, Object source, String target, Object value);

    public boolean isCompatible(Map<String, Object> args) {
        for(Map.Entry<String, Object> ent: args.entrySet()) {
            if (Attributes.get(ent.getKey()) == ent.getValue()) {
                return false;
            }
        }
        return true;
    }

    public Map<String, Object> toData() {
        Map<String, Object> dat = new LinkedHashMap<>();
        dat.put("Name", Name);
        dat.putAll(Attributes);
        return dat;
    }
}
