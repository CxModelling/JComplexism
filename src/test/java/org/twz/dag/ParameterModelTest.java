package org.twz.dag;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ParameterModelTest {
    private BayesNet BN;
    private NodeSet NS;
    private Map<String, Double> Exo;
    @Before
    public void setUp() {
        BN = new BayesNet("Test1");
        BN.appendLoci("Region");
        BN.appendLoci("Age = 15");
        BN.appendLoci("Rain ~ binom(1, 0.5)");
        BN.appendLoci("beta = 0.5");
        BN.appendLoci("mu = 0.5 * Region + beta*Age + Rain");
        BN.appendLoci("x ~ norm(mu, 0.01)");

        NS = new NodeSet("Area", new String[]{});
        NS.appendChild(
                new NodeSet("Ag1", new String[]{"Age", "x"}));

        NS.appendChild(
                new NodeSet("Ag2", new String[]{"Age"}, new String[]{"x"}));

        BN.print();
        NS.injectGraph(BN);
        NS.print();

        Exo = new HashMap<>();
        Exo.put("Region", 1.0);
    }

    @Test
    public void toPM() throws JSONException {
        ParameterModel pm = BN.toParameterModel(NS);

        System.out.println(pm.toJSON().toString(2));
    }

    @Test
    public void generate() {
        ParameterModel pm = BN.toParameterModel(NS);
        Parameters ps = pm.generate("A", Exo);
        ps.breed("B1", "Ag1");
        Parameters c1 = ps.breed("C1", "Ag2");
        ps.deepPrint();

        System.out.println(c1.getDouble("x"));

        Parameters c2 = ps.breed("C2", "Ag2");
        c2.detachFromParent(true);
        c2.deepPrint();
    }
}