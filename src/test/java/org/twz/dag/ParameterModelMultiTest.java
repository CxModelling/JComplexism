package org.twz.dag;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.twz.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

public class ParameterModelMultiTest {
    private BayesNet BN;
    private NodeSet NS;
    private Map<String, Double> Exo;
    @Before
    public void setUp() throws ValidationException {
        BN = new BayesNet("Test");
        BN.appendLoci("beta ~ unif(1, 20)");
        BN.appendLoci("Age = 15");
        BN.appendLoci("year");

        BN.appendLoci("r_act ~ unif(0.1, 0.3)");
        BN.appendLoci("act ~ exp(r_act)");
        BN.appendLoci("rec1 ~ k(Age)");
        BN.appendLoci("rec2 ~ k(20)");
        BN.appendLoci("z = step(5.0, year, rec1, rec2)");


        NS = new NodeSet("mm", new String[]{});
        NS.appendChild(
                new NodeSet("ebm", new String[]{"r_act", "beta"}));

        NS.appendChild(
                new NodeSet("abm", new String[]{"Age"}, new String[]{"z", "act"}));

        //BN.print();
        NS.injectGraph(BN);
        //NS.print();
        Exo = new HashMap<>();
        Exo.put("year", 0.0);
    }

    @Test
    public void toPM() throws Exception {
        ParameterModel pm = BN.toParameterModel(NS);

        System.out.println(pm.toJSON().toString(2));

    }

    @Test
    public void generate() throws ValidationException {
        ParameterModel pm = BN.toParameterModel(NS);
        Parameters ps = pm.generate("A", Exo);
        Parameters pe = ps.breed("EBM", "ebm");
        Parameters pa = ps.breed("ABM", "abm");

        ps.deepPrint();
        Assert.assertEquals(pa.getDouble("z"), 15.0, 1e-5);
        ps.impulse("year", 5);
        Assert.assertEquals(pa.getDouble("year"), 5.0, 1e-5);
        System.out.println(pa.getDouble("z"));
        Assert.assertEquals(pa.getDouble("z"), 20.0, 1e-5);
        ps.impulse("year", 6);
        Assert.assertEquals(pa.getDouble("year"), 6.0, 1e-5);
        Assert.assertEquals(pa.getDouble("z"), 20.0, 1e-5);
        Assert.assertEquals(pa.getDouble("r_act"), pe.getDouble("r_act"), 1e-5);

    }
}