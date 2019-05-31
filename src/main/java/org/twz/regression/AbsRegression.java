package org.twz.regression;

import org.twz.prob.IDistribution;

import java.util.Map;

public abstract class AbsRegression {

    public abstract String getVariableType();

    public abstract double predict(Map<String, Double> xs);

    public abstract IDistribution getSampler(Map<String, Double> xs);
}
