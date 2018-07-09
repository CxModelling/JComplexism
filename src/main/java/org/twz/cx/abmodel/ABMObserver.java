package org.twz.cx.abmodel;

import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.mcore.AbsObserver;
import org.twz.statespace.Transition;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class ABMObserver extends AbsObserver<ABModel> {
    private class Record {
        String Ag;
        Object Executed;
        double Time;
        Record(String ag, Object evt, double ti) {
            Ag = ag; Executed = evt; Time = ti;
        }
    }

    private Set<AbsBehaviour> ObsBehaviours;
    private Set<Object> ObsEvents;
    private Set<IObsFun<ABModel>> ObsFunctions;
    private ArrayList<Record> Records;

    ABMObserver() {
        ObsBehaviours = new LinkedHashSet<>();
        Records = new ArrayList<>();
        ObsFunctions = new LinkedHashSet<>();
    }


    void addObsEvent(Object evt) {
        ObsEvents.add(evt);
    }

    void addObsFunction(IObsFun<ABModel> fn) {
        ObsFunctions.add(fn);
    }

    void addObsBehaviour(AbsBehaviour be) {
        ObsBehaviours.add(be);
    }


    @Override
    protected void readStatics(ABModel model, Map<String, Double> tab, double ti) {
        for (AbsBehaviour be: ObsBehaviours) {
            be.fillData(tab, model, ti);
        }
        for (IObsFun<ABModel> fn: ObsFunctions) {
            fn.call(tab, model, ti);
        }
    }

    @Override
    public void updateDynamicObservations(ABModel model, Map<String, Double> flows, double ti) {
        ObsEvents.forEach(evt -> flows.put(evt.toString(), 0.0 + Records.stream().filter(e-> e.Executed == evt).count()));
    }

    @Override
    protected void clearFlows() {
        super.clearFlows();
        Records.clear();
    }

    public void record(AbsAgent ag, Transition tr, double time) {
        Records.add(new Record(ag.getName(), tr, time));
    }
}
