package org.twz.dataframe.timeseries;

import java.util.List;
import java.util.stream.Collectors;

public class DoubleSeries extends Series<Double> {

    public DoubleSeries(String name, List<Double> vs) {
        super(name, vs);
    }

    public IntegerSeries toIntegerSeries() {
        return new IntegerSeries(getName(),
                this.stream().map(Double::intValue).collect(Collectors.toList()));
    }

    public StringSeries toStringSeries() {
        return new StringSeries(getName(),
                this.stream().map(e->""+e).collect(Collectors.toList()));
    }

    @Override
    public String getDataType() {
        return "Double";
    }


    @Override
    public Double interpolate(double t, double t0, Double v0, double t1, Double v1) {
        return v0 + (v1 - v0) * (t-t0) / (t1-t0);
    }
}
