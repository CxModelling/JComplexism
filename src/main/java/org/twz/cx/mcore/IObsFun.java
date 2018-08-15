package org.twz.cx.mcore;


import java.util.Map;

/**
 * Created by TimeWz on 09/07/2018.
 */
public interface IObsFun<T extends AbsSimModel> {
    void call(Map<String, Double> tab, T model, double ti);
}
