package org.twz.dag.loci;

import org.junit.Test;
import org.mariuszgromada.math.mxparser.Function;
import org.mariuszgromada.math.mxparser.FunctionExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class FunctionLociTest {

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
    public void sample() {

        FunctionLoci fl =  new FunctionLoci("A", "a(4)");
        fl.linkToParentFunction("a", new a());
        System.out.println(fl.sample(new HashMap<>()));

    }
}