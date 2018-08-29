package org.twz.cx.element;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.dag.Gene;
import org.twz.io.AdapterJSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ModelAtom implements Comparable<ModelAtom>, AdapterJSONObject {
    private final String Name;
    protected Gene Parameters;
    protected JSONObject Attributes;
    private Event Next;
    private AbsScheduler Scheduler;

    public ModelAtom(String name, Gene parameters) {
        Name = name;
        Parameters = parameters;
        Attributes = new JSONObject();
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

    public Object get(String s) throws JSONException {
        return Attributes.get(s);
    }

    public String getString(String s) throws JSONException {
        return Attributes.getString(s);
    }

    public Double getDouble(String s) throws JSONException {
        return Attributes.getDouble(s);
    }

    public double getParameter(String key) {
        return Parameters.get(key);
    }

    public Gene getParameters() {
        return Parameters;
    }

    void setScheduler(AbsScheduler scheduler) {
        Scheduler = scheduler;
    }

    void detachScheduler() {
        Scheduler = null;
    }

    public void put(String key, Object value) throws JSONException {
        Attributes.put(key, value);
    }

    public void updateAttributes(Map<String, Object> atr) {
        atr.forEach((k, v) -> {
            try {
                Attributes.put(k, v);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
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

    public abstract void initialise(double ti, AbsSimModel model) throws JSONException;

    public abstract void reset(double ti, AbsSimModel model);

    public abstract void shock(double ti, AbsSimModel model, String action, JSONObject value) throws JSONException;

    public boolean isCompatible(Map<String, Object> args) {
        for(Map.Entry<String, Object> ent: args.entrySet()) {
            try {
                if (Attributes.get(ent.getKey()) != ent.getValue()) {
                    return false;
                }
            } catch (JSONException e) {
                return false;
            }
        }
        return true;
    }

    public Map<String, Object> toData() throws JSONException {
        Map<String, Object> dat = new LinkedHashMap<>();
        dat.put("Name", Name);
        String[] ks = JSONObject.getNames(Attributes);
        for (String key : ks) {
            dat.put(key, Attributes.get(key));
        }
        return dat;
    }

    @Override
    public int compareTo(ModelAtom o) {
        return Double.compare(getTTE(), o.getTTE());
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Name", Name);
        js.put("Attributes", Attributes);
        return js;
    }
}
