package org.twz.cx.ebmodel;

import org.twz.cx.mcore.AbsObserver;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IObsFun;
import org.twz.io.IO;
import org.twz.prob.T;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EBMObserver extends AbsObserver<AbsEquationBasedModel> {
    List<String> Stocks;
    List<IObsFun> StockFns, FlowFns;

    public EBMObserver() {
        Stocks = new ArrayList<>();
        StockFns = new ArrayList<>();
        FlowFns = new ArrayList<>();
    }

    public void addObservingStock(String stock) {
        Stocks.add(stock);
    }

    public void addObservingStockFunction(IObsFun fn) {
        StockFns.add(fn);
    }

    public void addObservingFlowFunction(IObsFun fn) {
        FlowFns.add(fn);
    }

    @Override
    protected void readStatics(AbsEquationBasedModel model, Map tab, double ti) {
        //todo
    }

    @Override
    public void updateDynamicObservations(AbsEquationBasedModel model, Map flows, double ti) {
//todo
    }
}
