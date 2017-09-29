package hgm.multimodel;

import hgm.util.DataFrame;
import mcore.AbsObserver;
import mcore.AbsSimModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class ObsMM extends AbsObserver<MultiModel> {
    private Map<String, AbsObserver> Observers;
    private Set<String> FullObs;
    private boolean All;

    public ObsMM() {
        super();
        FullObs = new HashSet<>();
        All = false;
    }

    public void setModels(Map<String, AbsSimModel> mods) {
        Observers = mods.values().stream()
                .collect(Collectors.toMap(AbsSimModel::getName, AbsSimModel::getObserver));
    }

    public void aggregationOn() {
        All = false;
    }

    @Override
    public void initialiseObservation(MultiModel model, double ti) {

        for (AbsSimModel m: model.getModels().values()) {
            m.initialiseObservation(ti);
        }
        model.getSummariser().readObs(model);
        model.crossImpulse(ti);
        updateObservation(model, ti);
    }

    @Override
    public void updateObservation(MultiModel model, double ti) {
        for (AbsSimModel m: model.getModels().values()) {
            m.updateObservation(ti);
        }
        model.getSummariser().readObs(model);
        Current.putAll(model.getSummariser().getSummary());
    }

    public void addObsModel(String mod) {
        FullObs.add(mod);
    }

    @Override
    public DataFrame getObservation() {
        List<Map<String, Double>> ts = getTimeSeries().stream().map(HashMap::new).collect(Collectors.toList());
        int size = ts.size();

        for (String m: FullObs) {
            AbsObserver ob = Observers.get(m);
            for (int i = 0; i < size; i++) {
                Map<String, Double> st = ob.getEntry(i);
                ts.get(i).putAll(st.entrySet().stream()
                        .filter(e->!e.getKey().equals("Time"))
                        .collect(Collectors.toMap(e->m+"@"+e.getKey(), Map.Entry::getValue)));
            }
        }

        if (All) {
            Set<String> cols = new HashSet<>();
            for (AbsObserver obs: Observers.values()) {
                cols.addAll(obs.getCurrent().keySet());
            }
            cols.remove("Time");

            for (int i = 0; i < size; i++) {
                Map<String, Double> st = ts.get(i);
                double v;
                for (String col: cols) {
                    v = 0;
                    for (AbsObserver obs: Observers.values()) {
                        v += (Double) obs.getEntry(i).get(col);
                    }
                    st.put(col, v);
                }
            }
        }
        return new DataFrame(ts, "Time");
    }
}
