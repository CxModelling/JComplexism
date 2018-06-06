package org.twz.cx.abmodel;

import org.twz.statespace.State;
import org.twz.statespace.Transition;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.mcore.*;

import java.util.*;


/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class ObserverABM extends AbsObserver<AgentBasedModel> {
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
    private ArrayList<Record> Records;

    ObserverABM() {
        ObsStates = new LinkedHashSet<>();
        ObsTransitions = new LinkedHashSet<>();
        ObsBehaviours = new LinkedHashSet<>();
        Records = new ArrayList<>();
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


    @Override
    protected void readStatics(AgentBasedModel model, Map<String, Double> tab, double ti) {
        for (State st: ObsStates) {
            tab.put(st.getName(), 0.0 + model.getPopulation().count(st));
        }
        for (AbsBehaviour be: ObsBehaviours) {
            be.fill(tab, model, ti);
        }
    }

    @Override
    public void updateDynamicObservations(AgentBasedModel model, Map<String, Double> flows, double ti) {
        ObsTransitions.forEach(tr-> flows.put(tr.getName(), 0.0+ Records.stream().filter(e-> e.Tr==tr).count()));
    }

    @Override
    protected void clearFlows() {
        super.clearFlows();
        Records.clear();
    }

    public void record(Agent ag, Transition tr, double time) {
        Records.add(new Record(ag.getName(), tr, time));
    }
}
