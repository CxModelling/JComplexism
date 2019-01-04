package org.twz.dataframe.timeseries;

import org.twz.datastructure.ProbabilityTable;

import java.util.List;
import java.util.stream.Collectors;

public class ProbabilityTableSeries extends Series<ProbabilityTable>  {
    public ProbabilityTableSeries(String name, String[] labels, List<double[]> vs) {
        super(name, array2pt(labels, vs));
    }

    private static List<ProbabilityTable> array2pt(String[] labels, List<double[]> vs) {
        return vs.stream().map(v->new ProbabilityTable(labels, v)).collect(Collectors.toList());
    }
}
