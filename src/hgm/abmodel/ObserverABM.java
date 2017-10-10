package hgm.abmodel;

import dcore.State;
import dcore.Transition;
import hgm.abmodel.behaviour.AbsBehaviour;
import mcore.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class ObserverABM extends AbsObserver<AgentBasedModel> {
    private class Record {
        protected String Ag;
        protected Transition Tr;
        protected double Time;
        public Record(String ag, Transition tr, double ti) {
            Ag = ag; Tr = tr; Time = ti;
        }
    }

    private Set<State> ObsStates;
    private Set<Transition> ObsTransitions;
    private Set<AbsBehaviour> ObsBehaviours;
    private ArrayList<Record> Records;

    public ObserverABM() {
        ObsStates = new LinkedHashSet<>();
        ObsTransitions = new LinkedHashSet<>();
        ObsBehaviours = new LinkedHashSet<>();
        Records = new ArrayList<>();
    }

    public void addObsState(State st) {
        ObsStates.add(st);
    }

    public void addObsTransition(Transition tr) {
        ObsTransitions.add(tr);
    }

    public void addObsBehaviour(AbsBehaviour be) {
        ObsBehaviours.add(be);
    }

    @Override
    public void initialiseObservation(AgentBasedModel model, double ti) {
        for (State st: ObsStates) {
            Current.put(st.getName(), model.getPopulation().count(st));
        }
        for (AbsBehaviour be: ObsBehaviours) {
            be.fill(Current, model, ti);
        }
    }

    @Override
    public void updateObservation(AgentBasedModel model, double ti) {
        for (State st: ObsStates) {
            Current.put(st.getName(), model.getPopulation().count(st));
        }

        double t0 = (Last.containsKey("Time"))?Last.get("Time"):Double.POSITIVE_INFINITY;

        List<Record> r0 = Records.stream().filter(e->e.Time < t0).collect(Collectors.toList()),
                     r1 = Records.stream().filter(e->e.Time >= t0).collect(Collectors.toList());

        Records.clear();

        for (Transition tr: ObsTransitions) {
            double count = 0;
            try {
                count = Last.get(tr.getName());
            } catch (NullPointerException ex) {
                count = 0;
            } finally {
                count += r0.stream().filter(e->e.Tr==tr).count();
                Last.put(tr.getName(), count);
            }

            try {
                count = Current.get(tr.getName());
            } catch (NullPointerException ex) {
                count = 0;
            } finally {
                count += r1.stream().filter(e->e.Tr==tr).count();
                Current.put(tr.getName(), count);
            }
        }

        for (AbsBehaviour be: ObsBehaviours) {
            be.fill(Current, model, ti);
        }
    }

    public void record(Agent ag, Transition tr, double time) {
        Records.add(new Record(ag.getName(), tr, time));
    }
}
