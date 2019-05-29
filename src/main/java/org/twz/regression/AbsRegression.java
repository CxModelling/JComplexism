package org.twz.regression;

import org.twz.dag.Chromosome;

import java.util.Map;

public abstract class AbsRegression {

    public abstract String getVariableType();

    public double predict(Map<String, Double> xs) {
        Chromosome chr = new Chromosome(xs);
        return predict(chr);
    }

    public abstract double predict(Chromosome xs);
}
