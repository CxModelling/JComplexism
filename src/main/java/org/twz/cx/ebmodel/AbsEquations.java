package org.twz.cx.ebmodel;

import org.twz.cx.element.Event;
import org.twz.cx.element.ModelAtom;
import org.twz.cx.element.Ticker.AbsTicker;
import org.twz.cx.element.Ticker.StepTicker;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.dag.Chromosome;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbsEquations extends ModelAtom {
    private final String[] YNames;
    private double[] Ys;
    private Map<String, Integer> YIndices;
    private double Last;
    private AbsTicker Clock;

    public AbsEquations(String name, String[] y_names, Chromosome parameters, double dt) {
        super(name, parameters);
        Clock = new StepTicker(name, dt);
        YNames = y_names;
        Last = 0;
        Ys = new double[y_names.length];
        YIndices = new HashMap<>();
        for (int i = 0; i < YNames.length; i++) {
            YIndices.put(YNames[i], i);
        }
    }

    public AbsEquations(String name, String[] y_names, Map<String, Double> parameters, double dt) {
        super(name, parameters);
        Clock = new StepTicker(name, dt);
        YNames = y_names;
        Last = 0;
        Ys = new double[y_names.length];
        YIndices = new HashMap<>();
        for (int i = 0; i < YNames.length; i++) {
            YIndices.put(YNames[i], i);
        }
    }

    public AbsEquations(String name, String[] y_names, double dt) {
        super(name);
        Clock = new StepTicker(name, dt);
        YNames = y_names;
        Last = 0;
        Ys = new double[y_names.length];
        YIndices = new HashMap<>();
        for (int i = 0; i < YNames.length; i++) {
            YIndices.put(YNames[i], i);
        }
    }

    public int getDimension() {
        return YNames.length;
    }

    @Override
    public void initialise(double ti, AbsSimModel model) {
        Last = ti;
        Clock.initialise(ti);
    }

    @Override
    public void reset(double ti, AbsSimModel model) {
        Last = ti;
        Clock.update(ti);
    }
    @Override
    protected Event findNext() {
        return new Event("update", Clock.getNext());
    }

    @Override
    public void updateTo(double ti) {
        goTo(ti);
        Clock.update(Last);
        dropNext();
    }

    @Override
    public void executeEvent() {
        updateTo(getTTE());
    }

    void goTo(double t1) {
        double t0 = Last;
        if (t0 >= t1) return;
        goTo(t0, Ys, t1, Ys);
        Last = t1;
    }

    protected abstract void goTo(double t0, double[] y0, double t1, double[] y1);

    void measure(Map<String, Double> tab, EBMMeasurement measurement) {
        measurement.call(tab, Last, Ys, getParameters(), Attributes);
    }

    public void setY(double[] y) {
        System.arraycopy(y, 0, Ys, 0, getDimension());
    }

    public void setY(Map<String, Double> y) {
        y.forEach((k, v)->Ys[YIndices.get(k)] = v);
    }

    public Map<String, Double> getDictY() {
        return YIndices.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e->Ys[e.getValue()]));
    }

    public double getY(String y) {
        assert YIndices.containsKey(y);
        return Ys[YIndices.get(y)];
    }

    String[] getYNames() {
        return YNames;
    }

    void setY(String y, double v) {
        Ys[YIndices.get(y)] = v;
    }


}
