package org.twz.cx.abmodel;


import org.twz.cx.mcore.*;
import org.json.JSONObject;
import org.twz.dag.ParameterCore;

import java.util.*;


/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class ABModel extends AbsAgentBasedModel {


    public ABModel(String name, ParameterCore env, org.twz.cx.abmodel.Population pop, IY0 protoY0) {
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
    protected void record(AbsAgent ag, Object todo, double time) {
        ((ABMObserver) Observer).record(ag, todo, time);
    }
}
