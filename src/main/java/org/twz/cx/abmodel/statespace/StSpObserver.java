package org.twz.cx.abmodel.statespace;

import org.json.JSONException;
import org.twz.cx.abmodel.*;
import org.twz.dataframe.Pair;
import org.twz.statespace.State;
import org.twz.statespace.Transition;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.mcore.*;

import java.util.*;


/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class StSpObserver extends AbsObserver<StSpABModel> {
    private class Record {
        String Ag;
        Transition Tr;
        double Time;
        Record(String ag, Transition tr, double ti) {
            Ag = ag; Tr = tr; Time = ti;
        }
    }

    private Set<State> ObsStates;
    private Set<Transition> ObsTransitions;
    private Set<AbsBehaviour> ObsBehaviours;
    private Set<IObsFun<StSpABModel>> ObsFunctions;
    private ArrayList<Record> Records;
    private Map<String, Pair<String, Object>> LazySnapshot;

    StSpObserver() {
        ObsStates = new LinkedHashSet<>();
        ObsTransitions = new LinkedHashSet<>();
        ObsBehaviours = new LinkedHashSet<>();
        ObsFunctions = new LinkedHashSet<>();
        Records = new ArrayList<>();
    }

    @Override
    public void initialiseObservations(StSpABModel model, double ti) {
        super.initialiseObservations(model, ti);
        locateLazySnapshots(model, ti);
    }

    @Override
    protected void readStatics(StSpABModel model, Map<String, Double> tab, double ti) {
        //Map<String, Object> option = new HashMap<>();
        for (State st: ObsStates) {
            tab.put(st.getName(), 0.0 + model.getPopulation().count("State", st));
        }
        for (AbsBehaviour be: ObsBehaviours) {
            be.fillData(tab, model, ti);
        }
        for (IObsFun<StSpABModel> fn: ObsFunctions) {
            fn.call(tab, model, ti);
        }
    }

    @Override
    public void updateDynamicObservations(StSpABModel model, Map<String, Double> flows, double ti) {
        ObsTransitions.forEach(tr-> flows.put(tr.getName(), 0.0+ Records.stream().filter(e-> e.Tr==tr).count()));
    }

    void addObsState(State st) {
        ObsStates.add(st);
    }

    void addObsTransition(Transition tr) {
        ObsTransitions.add(tr);
    }

    void addObsBehaviour(AbsBehaviour be) {
        ObsBehaviours.add(be);
    }

    void addObsFunction(IObsFun<StSpABModel> fn) {
        ObsFunctions.add(fn);
    }

    @Override
    public double getSnapshot(StSpABModel model, String key, double ti) {
        if (LazySnapshot.containsKey(key)) {
            Pair<String, Object> ent = LazySnapshot.get(key);
            if (ent.getFirst().equals("State")) {
                return 0.0 + model.getPopulation().count("State", ent.getValue());
            } else {
                Map<String, Double> temp = new HashMap<>();
                ((AbsBehaviour) ent.getSecond()).fillData(temp, model, ti);
                return temp.get(key);
            }
        }
        return super.getSnapshot(model, key, ti);
    }

    private void locateLazySnapshots(StSpABModel model, double ti) {
        LazySnapshot =  new HashMap<>();
        ObsStates.forEach(st->LazySnapshot.put(st.getName(), new Pair<>("State", st)));

        Map<String, Double> temp = new HashMap<>();
        for (AbsBehaviour behaviour : ObsBehaviours) {
            temp.clear();
            behaviour.fillData(temp, model, ti);
            temp.keySet().forEach(k->LazySnapshot.put(k, new Pair<>("Behaviour", behaviour)));
        }
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
