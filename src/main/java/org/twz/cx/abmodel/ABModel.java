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
public class ABModel extends AbsAgentBasedModel {


    public ABModel(String name, Map env, org.twz.cx.abmodel.Population pop, IY0 protoY0) {
        super(name, env, pop, new ABMObserver(), protoY0);
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public void readY0(IY0 y0, double ti) {
        Collection<JSONObject> entries = y0.get();
        JSONObject ajs;
        Map<String, Object> atr;
        for (JSONObject entry : entries) {
            ajs = entry.getJSONObject("attributes");
            atr = new HashMap<>();
            Iterator<?> keys = ajs.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();
                atr.put(key, ajs.get(key));
            }

            makeAgents(entry.getInt("n"), ti, atr);
        }
    }

    @Override
    public void validateRequests() {

    }

    @Override
    public void addListener(IEventListener listener) {

    }

    @Override
    public Double getSnapshot(String key, double ti) {
        return null;
    }
}
