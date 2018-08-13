package org.twz.cx.ebmodel;

import org.twz.cx.element.Event;
import org.twz.cx.element.ModelAtom;
import org.twz.cx.element.Ticker.AbsTicker;
import org.twz.cx.element.Ticker.StepTicker;
import org.twz.dag.Gene;

import java.util.Map;

public abstract class AbsEquations extends ModelAtom {
    private AbsTicker Clock;

    public AbsEquations(String name, Gene parameters, double dt) {
        super(name, parameters);
        Clock = new StepTicker(name, dt);
    }

    public AbsEquations(String name, Map<String, Double> parameters, double dt) {
        super(name, parameters);
        Clock = new StepTicker(name, dt);
    }

    public AbsEquations(String name, double dt) {
        super(name);
        Clock = new StepTicker(name, dt);
    }


    @Override
    protected Event findNext() {
        return new Event("update", Clock.getNext());
    }

    @Override
    public void updateTo(double ti) {
        goTo(ti);
        Clock.update(ti);
        dropNext();
    }

    @Override
    public void executeEvent() {
        updateTo(getTTE());
    }

    public abstract void goTo(double ti);

    public abstract void setY(double[] y);

    public abstract void setY(Map<String, Double> y);

    public abstract Map<String, Double> getDictY();

}
