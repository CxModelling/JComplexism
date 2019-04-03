package org.twz.dag.loci;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.twz.datafunction.PrAgeByYearSex;
import org.twz.datafunction.RateAgeByYearSex;
import org.twz.exception.IncompleteConditionException;
import org.twz.io.IO;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DistributionLociTest {

    DistributionLoci DL0, DL1, DL2;
    Map<String, Double> C1, C2;

    @Before
    public void setDL0 () {
        DL0 = new DistributionLoci("D0", "binom(5, 0.3)");
    }

    @Before
    public void setDL1 () {
        DL1 = new DistributionLoci("D1", "binom(5, p)");
        C1 = new HashMap<>();
        C1.put("p", 0.3);
    }

    @Before
    public void setDL2 () throws JSONException {
        DL2 = new DistributionLoci("D2", "pr(Year, Sex)");
        DL2.bindDataFunction("pr", new PrAgeByYearSex("pr",
                IO.loadJSON("src/test/resources/N_ys.json")));
        C2 = new HashMap<>();
        C2.put("Year", 2005.0);
        C2.put("Sex", 0.0);
    }

    @Test
    public void getParents() {
        Assert.assertTrue(DL1.getParents().contains("p"));
    }

    @Test
    public void evaluate() {
        C2.put("D2", 30.0);

    }

    @Test
    public void render() throws IncompleteConditionException {
        System.out.println(DL0.render());
        System.out.println(DL1.render(C1));
        System.out.println(DL2.render(C2));

    }

    @Test
    public void findDistribution() throws IncompleteConditionException {
        Assert.assertEquals("binom(5,0.3)", DL1.findDistribution(C1).getName());
    }

    @Test
    public void getDefinition() {
        Assert.assertEquals("D1~binom(5, p)", DL1.getDefinition());
    }

}