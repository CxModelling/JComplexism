package org.twz.datafunction;

import org.junit.Before;
import org.junit.Test;
import org.mariuszgromada.math.mxparser.FunctionExtension;
import org.twz.dag.BayesNet;
import org.twz.io.IO;

import java.util.HashMap;
import java.util.Map;

public class AbsDataFunctionTest {

    Map<String, AbsDataFunction> DataSets;
    BayesNet BN;

    @Before
    public void setUp() throws Exception {
        BN = new BayesNet("test");
        BN.appendLoci("year = 2010");
        BN.appendLoci("sex ~ binom(1, 0.1)");
        BN.appendLoci("age ~ pr(year, sex)");
        BN.appendLoci("rate = dr(year, sex, age)");
        BN.appendLoci("delay ~ exp(rate)");


        DataSets = new HashMap<>();
        DataSets.put("pr", new PrAgeByYearSex("pr",
                IO.loadJSON("src/test/resources/N_ys.json")));

        DataSets.put("dr", new RateAgeByYearSex("dr",
                IO.loadJSON("src/test/resources/D_ys.json")));

        BN.bindDataFunctions(DataSets);
    }

    @Test
    public void getName() {
        System.out.println(BN.sample());
    }
}