package org.twz.dataframe.timeseries;

import java.util.List;
import java.util.stream.Collectors;

public class StringSeries extends Series<String> {

    public StringSeries(String name, List<String> vs) {
        super(name, vs);
    }

    public IntegerSeries toIntegerSeries() {
        return new IntegerSeries(getName(),
                this.stream().map(Integer::parseInt).collect(Collectors.toList()));
    }

    public DoubleSeries toDoubleSeries() {
        return new DoubleSeries(getName(),
                this.stream().map(Double::parseDouble).collect(Collectors.toList()));
    }

    @Override
    public String getDataType() {
        return "String";
    }
}
