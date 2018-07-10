package org.twz.cx.multimodel;

import org.twz.cx.mcore.*;


import java.util.*;

/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class ObsMM extends AbsObserver<MultiModel> {

    private List<String> Observed;

    ObsMM() {
        super();
        Observed = new LinkedList<>();
    }

    protected void addObservedSelector(String sel) {
        Observed.add(sel);
    }

    @Override
    protected void readStatics(MultiModel model, Map<String, Double> tab, double ti) {
        Summariser s = model.getSummariser();
        boolean l = tab == getLast();
        try {
            tab.putAll(s.getImpulses());

            List<Map<String, Double>> sub;
            for (String sel: Observed) {
                sub = new ArrayList<>();

                for (AbsSimModel m: model.selectAll(sel).values()) {
                    sub.add((l)? m.getObserver().getLast(): m.getObserver().getMid());
                }

                fillTables(sel, tab, sub);
            }
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public void updateDynamicObservations(ModelSet model, Map<String, Double> flows, double ti) {
    }

    private void fillTables(String sel, Map<String, Double> tab, List<Map<String, Double>> sub) {
        Set<String> names = new LinkedHashSet<>();
        for (Map<String, Double> s: sub) {
            names.addAll(s.keySet());
        }
        Double v;
        names.remove("Time");
        for (String name: names) {
            v = sub.stream().mapToDouble(e-> e.getOrDefault(name, 0.0)).sum();
            tab.put(sel + "@" + name, v);
        }
    }

}
