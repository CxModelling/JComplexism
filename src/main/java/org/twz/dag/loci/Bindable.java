package org.twz.dag.loci;

import org.twz.datafunction.AbsDataFunction;
import org.twz.datafunction.DataCentre;

public interface Bindable {
    void bindDataCentre(DataCentre dataCentre);
    void bindDataFunction(String name, AbsDataFunction df);
}
