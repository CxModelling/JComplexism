package org.twz.cx.abmodel;

import org.twz.cx.element.Event;
import org.twz.cx.element.Request;
import org.twz.statespace.AbsDCore;
import org.twz.statespace.Transition;
import org.twz.cx.abmodel.behaviour.AbsBehaviour;
import org.twz.cx.mcore.*;
import org.json.JSONObject;

import java.util.*;


/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class AgentBasedModel extends LeafModel {
    private final Population Agents;
    private AbsDCore DCore;
    private Map<String, AbsBehaviour> Behaviours;

    public AgentBasedModel(String name, AbsDCore dc, String prefix) {
        super(name, new ObserverABM(), meta);
        Agents = new Population(dc, prefix);
        Behaviours = new LinkedHashMap<>();
        DCore = dc;
    }

    public void addObsState(String state) {
        ((ObserverABM) getObserver()).addObsState(DCore.getState(state));
    }

    public void addObsTransition(String transition) {
        ((ObserverABM) getObserver()).addObsTransition(DCore.getTransition(transition));
    }

    public void addObsBehaviour(String behaviour) {
        ((ObserverABM) getObserver()).addObsBehaviour(Behaviours.get(behaviour));
    }

    public AbsBehaviour getBehaviours(String be) {
        return Behaviours.get(be);
    }

    public Population getPopulation() {
        return Agents;
    }

    @Override
    public void reset(double ti) {

    }

    @Override
    public void findNext() {

    }

    @Override
    public void doRequest(Request req) {
        String nod = req.Who;
        Event evt = req.Todo;
        double time = req.getTime();

        if (Behaviours.containsKey(nod)) {
            AbsBehaviour be = Behaviours.get(nod);
            be.exec(this, evt);
        } else {
            Agent ag = Agents.get(nod);
            Transition tr = (Transition) evt.getValue();
            ((ObserverABM) getObserver()).record(ag, tr, time);
            // check transition self.check_tr(ag, tr)
            ag.exec(evt);
            // impulse transition self.impulse_tr(bes, ag, time)
            ag.update(time);
        }
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }
}
