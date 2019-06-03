package org.twz.dag;

import org.junit.Before;
import org.junit.Test;
import org.twz.dag.loci.FunctionLoci;
import org.twz.datafunction.AbsUserDefinedFunction;

import static org.junit.Assert.*;

public class BayesNetTest {
    private AbsUserDefinedFunction AgeGrouping = new AbsUserDefinedFunction("gp", 1) {
        @Override
        public double calculate() {
            double age = Selected[0];
            if (age < 15) {
                return 0;
            } else if (age < 35) {
                return 1;
            } else if (age < 65) {
                return 2;
            } else {
                return 3;
            }
        }
    };


    private BayesNet bn1, bn2, bn3;

    @Before
    public void setUp() throws Exception {
        bn1 = new BayesNet("Main");
        bn1.appendLoci("Age ~ unif(10, 20)");
        bn1.appendLoci("y = Age * 20");
        bn1.complete();

        bn2 = new BayesNet("Sub");
        bn2.appendLoci("x ~ norm(y, 1)");
        bn2.complete();

        bn3 = new BayesNet("UserDefined");
        bn3.appendLoci("Age ~ unif(0, 100)");
        bn3.appendLoci("AgeGroup = gp(Age)");
        bn3.complete();
    }

    @Test
    public void join() {
        System.out.println("Bayesian Network 1");
        System.out.println(bn1.toString());

        System.out.println("Bayesian Network 2");
        System.out.println(bn2.toString());

        bn1.join(bn2);
        System.out.println("The merged");
        System.out.println(bn1.toString());
    }

    @Test
    public void merge() {
        System.out.println("Bayesian Network 1");
        System.out.println(bn1.toString());

        System.out.println("Bayesian Network 2");
        System.out.println(bn2.toString());

        BayesNet merged = BayesNet.merge("Merged", bn1, bn2);
        System.out.println("The merged");
        System.out.println(merged.toString());
    }

    @Test
    public void userDefined() {
        ((FunctionLoci) bn3.getLoci("AgeGroup")).bindDataFunction("gp", AgeGrouping);
        System.out.println(bn3.sample());
    }

}