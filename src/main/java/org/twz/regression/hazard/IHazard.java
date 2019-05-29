package org.twz.regression.hazard;

public interface IHazard {
    double cumulativeHazard(double t);
    double inverseCumulativeHazard(double h);
}
