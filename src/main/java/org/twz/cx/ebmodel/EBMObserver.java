package org.twz.cx.ebmodel;

import org.twz.cx.mcore.AbsObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EBMObserver extends AbsObserver<EquationBasedModel> {
    List<String> Stocks;
    List<EBMMeasurement> StockFns, FlowFns;

    public EBMObserver() {
        Stocks = new ArrayList<>();
        StockFns = new ArrayList<>();
        FlowFns = new ArrayList<>();
    }

    public void addObservingStock(String stock) {
        Stocks.add(stock);
    }

    public void addObservingStockFunction(EBMMeasurement fn) {
        StockFns.add(fn);
    }

    public void addObservingFlowFunction(EBMMeasurement fn) {
        FlowFns.add(fn);
    }

    @Override
    protected void readStatics(EquationBasedModel model, Map<String, Double> tab, double ti) {
        double tiu;
        if (tab == getMid()) {
            tiu = ti - ObservationalInterval / 2;
        } else {
            tiu = ti;
        }
        model.goTo(tiu);
        AbsEquations Eq = model.getEquations();
        Stocks.forEach(s->tab.put(s, Eq.getY(s)));
        StockFns.forEach(sf->Eq.measure(tab, sf));
    }

    @Override
    public void updateDynamicObservations(EquationBasedModel model,  Map<String, Double> flows, double ti) {
        AbsEquations Eq = model.getEquations();
        FlowFns.forEach(sf->Eq.measure(flows, sf));
    }
}
