package hgm.multimodel;

import mcore.*;


import java.util.*;

/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class ObsMM extends AbsObserver<ModelSet> {

    ObsMM() {
        super();

    }

    @Override
    protected void readStatics(ModelSet model, Map<String, Double> tab, double ti) {
        Summariser s = model.getSummariser();
        s.summarise(ti);
        try {
            tab.putAll(s.getSummary());
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public void updateDynamicObservations(ModelSet model, Map<String, Double> flows, double ti) {
    }

}
