package org.twz.dataframe.timeseries;

import java.util.List;
import java.util.stream.Collectors;

public class IntegerSeries extends Series<Integer> {
    public IntegerSeries(String name, List<Integer> vs) {
        super(name, vs);
    }

    public DoubleSeries toDoubleSeries() {
        return new DoubleSeries(getName(),
                this.stream().map(Integer::doubleValue).collect(Collectors.toList()));
    }

    public StringSeries toStringSeries() {
        return new StringSeries(getName(),
                this.stream().map(e->""+e).collect(Collectors.toList()));
    }

    @Override
    public String getDataType() {
        return "Integer";
    }

    @Override
    public Integer interpolate(double t, double t0, Integer v0, double t1, Integer v1) {
        return v0 + (int)((t-t0) / (t1-t0) * (v1 - v0));
    }
}
