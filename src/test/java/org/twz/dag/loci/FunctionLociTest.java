package org.twz.dag.loci;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mariuszgromada.math.mxparser.FunctionExtension;
import org.twz.dag.Chromosome;
import org.twz.exception.IncompleteConditionException;

import java.util.HashMap;
import java.util.Map;

public class FunctionLociTest {
    FunctionLoci Loci1, Loci2;

    @Before
    public void setUp() throws Exception {
        Loci1 = new FunctionLoci("A", "a(4)");
        Loci2 = new FunctionLoci("B", "min(5, x)");
    }

    @Test
    public void getParents() {
        Assert.assertTrue(Loci2.getParents().contains("x"));
    }

    @Test
    public void evaluate() {
        Assert.assertEquals(Loci2.evaluate((Chromosome) null), 0.0);
    }

    @Test
    public void render() throws IncompleteConditionException {
        Assert.assertEquals(Double.NaN, Loci2.render());
        Map<String, Double> cond = new HashMap<>();
        cond.put("x", 3.0);
        Assert.assertEquals(3.0, Loci2.render(cond));
    }

    @Test
    public void getDefinition() {
        Assert.assertEquals("B=min(5, x)", Loci2.getDefinition());
    }

    class a implements FunctionExtension {
        private double x = Double.NaN;
        @Override
        public int getParametersNumber() {
            return 1;
        }

        @Override
        public void setParameterValue(int i, double v) {
            x = v;
        }

        @Override
        public String getParameterName(int i) {
            return "x";
        }

        @Override
        public double calculate() {
            return x + 5;
        }

        @Override
        public FunctionExtension clone() {
            return null;
        }
    }

    @Test
    public void sample() throws IncompleteConditionException{

        Loci1.linkToParentFunction("a", new a());
        Assert.assertEquals(9.0, Loci1.render());

    }
}