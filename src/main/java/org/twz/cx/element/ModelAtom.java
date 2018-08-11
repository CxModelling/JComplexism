package org.twz.cx.element;

import org.json.JSONObject;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.dag.Gene;
import org.twz.io.AdapterJSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ModelAtom implements Comparable<ModelAtom>, AdapterJSONObject {
    private final String Name;
    protected Gene Parameters;
    protected Map<String, Object> Attributes;
    private Event Next;
    private AbsScheduler Scheduler;

    public ModelAtom(String name, Gene parameters) {
        Name = name;
        Parameters = parameters;
        Attributes = new HashMap<>();
        Next = Event.NullEvent;
    }

    public ModelAtom(String name, Map<String, Double> parameters) {
        this(name, new Gene(parameters));
    }

    public ModelAtom(String name) {
        this(name, new Gene());
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

    public double getParameter(String key) {
        return Parameters.get(key);
    }

    public void setScheduler(AbsScheduler scheduler) {
        Scheduler = scheduler;
    }

    public void detachScheduler() {
        Scheduler = null;
    }

    public void put(String key, Object value) {
        Attributes.put(key, value);
    }

    public void updateAttributes(Map<String, Object> atr) {
        Attributes.putAll(atr);
    }

    public Event getNext() {
        if (Next.isCancelled()) {
            Next = findNext();
        }
        assert Next != null;
        return Next;
    }

    public double getTTE() {
        return getNext().getTime();
    }

    protected abstract Event findNext();

    public void dropNext() {
        Next.cancel();
        try {
            Scheduler.await(this);
        } catch (NullPointerException ignored) {}
    }

    public void approveEvent(Event evt) {
        Next = evt;
    }

    public void disapproveEvent(double ti) {
        updateTo(ti);
    }

    public abstract void updateTo(double ti);

    public abstract void executeEvent();

    public abstract void initialise(double ti, AbsSimModel model);

    public abstract void reset(double ti, AbsSimModel model);

    public abstract void shock(double ti, Object source, String target, Object value);

    public boolean isCompatible(Map<String, Object> args) {
        for(Map.Entry<String, Object> ent: args.entrySet()) {
            if (Attributes.get(ent.getKey()) != ent.getValue()) {
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

    @Override
    public int compareTo(ModelAtom o) {
        return Double.compare(getTTE(), o.getTTE());
    }

    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("Name", Name);
        js.put("Attributes", new JSONObject(Attributes));
        return js;
    }
}
