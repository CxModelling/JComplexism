package org.twz.cx.mcore;


import java.util.Map;

/**
 * Created by TimeWz on 09/07/2018.
 */
public interface IObsFun {
    void call(Map<String, Double> tab, AbsSimModel model, double ti);
}
