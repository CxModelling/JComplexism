package org.twz.cx.element;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.dag.Chromosome;
import org.twz.exception.IncompleteConditionException;
import org.twz.io.AdapterJSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelAtom implements Comparable<ModelAtom>, AdapterJSONObject {
    private final String Name;
    protected Chromosome Parameters;
    protected Map<String, Object> Attributes;
    private Event Next;
    private AbsScheduler Scheduler;

    public ModelAtom(String name, Chromosome parameters) {
        Name = name;
        Parameters = parameters;
        Attributes = new HashMap<>();
        Next = Event.NullEvent;
    }

    public ModelAtom(String name, Map<String, Double> parameters) {
        this(name, new Chromosome(parameters));
    }

    public ModelAtom(String name) {
        this(name, Chromosome.nullChromosome);
    }

    public String getName() {
        return Name;
    }

    public Object get(String s) {
        return Attributes.get(s);
    }

    public String getString(String s) {
        return Attributes.get(s).toString();
    }

    public Double getDouble(String s) {
        Object o = Attributes.get(s);
        if (o instanceof Integer) {
            return 0.0 + ((Integer) o).doubleValue();
        }

        try {
            return (double) o;
        } catch (ClassCastException e) {
            return Double.NaN;
        }
    }

    public void setParameters(Chromosome parameters) {
        Parameters = parameters;
    }

    public double getParameter(String key) {
        return Parameters.getDouble(key);
    }

    public Chromosome getParameters() {
        return Parameters;
    }

    void setScheduler(AbsScheduler scheduler) {
        Scheduler = scheduler;
    }

    void detachScheduler() {
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

    public abstract void updateTo(double ti) throws IncompleteConditionException;

    public abstract void executeEvent();

    public abstract void initialise(double ti, AbsSimModel model) throws JSONException;

    public abstract void reset(double ti, AbsSimModel model);

    public abstract void shock(double ti, AbsSimModel model, String action, JSONObject value) throws JSONException;

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
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Name", Name);
        js.put("Attributes", Attributes);
        return js;
    }

    public List<Disclosure> manageDisclosures(List<Disclosure> dis) {
        return dis;
    }
}
