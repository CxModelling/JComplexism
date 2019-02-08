package org.twz.dataframe.demographics;

import org.twz.dataframe.TimeSeries;
import org.twz.exception.TimeseriesException;

public abstract class AbsDemography {
    protected double StartTime, EndTime;
    protected TimeSeries Ts;

    public AbsDemography(TimeSeries ts) {
        Ts = ts;
        StartTime = ts.getStartTime();
        EndTime = ts.getEndTime();
    }

    public abstract double getPopulation(double time) throws TimeseriesException;

    public abstract double getDeathRate(double time) throws TimeseriesException;

    public abstract double getBirthRate(double time) throws TimeseriesException;

    public abstract double getMigration(double time) throws TimeseriesException;

    public abstract ISampler getPopulationSampler(double time) throws TimeseriesException;
}
