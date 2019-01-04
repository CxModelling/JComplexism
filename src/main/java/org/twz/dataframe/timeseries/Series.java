package org.twz.dataframe.timeseries;

import java.util.ArrayList;
import java.util.List;

public class Series<T> extends ArrayList<T> {
    private String Name;

    public Series(String name, List<T> vs) {
        super(vs);
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public String data_type() {
        return get(0).getClass().getSimpleName();
    }

}
