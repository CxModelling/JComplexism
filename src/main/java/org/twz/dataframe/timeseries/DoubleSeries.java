package org.twz.dataframe.timeseries;

import java.util.List;
import java.util.stream.Collectors;

public class DoubleSeries extends Series<Double>{
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
    public String data_type() {
        return "Double";
    }
}
