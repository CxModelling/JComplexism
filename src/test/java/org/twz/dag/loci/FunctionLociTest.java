package org.twz.dag.loci;

import org.junit.Before;
import org.junit.Test;
import org.mariuszgromada.math.mxparser.FunctionExtension;
import org.twz.dag.Chromosome;
import org.twz.datafunction.AbsDataFunction;
import org.twz.exception.IncompleteConditionException;
import org.twz.prob.IDistribution;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FunctionLociTest {
    FunctionLoci Loci1, Loci2, Loci3;

    @Before
    public void setUp() throws Exception {
        Loci1 = new FunctionLoci("A", "a(4)");
        Loci2 = new FunctionLoci("B", "min(5, x)");
        Loci3 = new FunctionLoci("mu", "exp(-0.4+sex*0.1)*exp(0.5)");
    }

    @Test
    public void getParents() {
        assertTrue(Loci2.getParents().contains("x"));
    }

    @Test
    public void evaluate() {
        assertEquals(Loci2.evaluate((Chromosome) null), 0.0, 1e-5);
    }

    @Test
    public void render() throws IncompleteConditionException {
        // assertEquals(Double.NaN, Loci2.render(), 1e-2);
        Map<String, Double> cond = new HashMap<>();
        cond.put("x", 3.0);
        assertEquals(3.0, Loci2.render(cond), 1e-2);
    }

    @Test
    public void getDefinition() {
        assertEquals("B=min(5, x)", Loci2.getDefinition());
    }

    class a extends AbsDataFunction {
        private double x = Double.NaN;

        public a(String name) {
            super(name, new String[] {"x"}, null);
        }

        @Override
        public IDistribution getSampler(double[] values) {
            return null;
        }

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

        Loci1.bindDataFunction("a", new a("a"));
        assertEquals(9.0, Loci1.render(), 1e-3);


    }

    @Test
    public void regression() throws Exception {
        Map<String, Double> ps = new HashMap<>();
        ps.put("sex", 1.0);

        System.out.println(Loci3.render(ps));
    }
}