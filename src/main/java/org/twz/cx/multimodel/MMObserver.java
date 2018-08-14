package org.twz.cx.multimodel;

import org.twz.cx.mcore.AbsObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by TimeWz on 14/08/2018.
 */
public class MMObserver extends AbsObserver<MultiModel> {
    private List<String> ObservingModels;

    public MMObserver() {
        ObservingModels = new ArrayList<>();
    }

    public void addObservingModel(String m) {
        ObservingModels.add(m);
    }

    @Override
    protected void readStatics(MultiModel model, Map<String, Double> tab, double ti) {
        if (tab == Last) {
            for (String m : ObservingModels) {
                model.getModel(m).getObserver().putAllLast(m, tab);
            }
        } else {
            for (String m : ObservingModels) {
                model.getModel(m).getObserver().putAllMid(m, tab);
            }
        }

    }

    @Override
    public void updateDynamicObservations(MultiModel model, Map<String, Double> flows, double ti) {
        for (String m : ObservingModels) {
            model.getModel(m).getObserver().putAllFlows(m, flows);
        }
    }
}
