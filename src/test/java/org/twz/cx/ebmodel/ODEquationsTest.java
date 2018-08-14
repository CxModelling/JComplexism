package org.twz.cx.ebmodel;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.junit.Before;
import org.junit.Test;
import org.twz.cx.abmodel.statespace.StSpY0;
import org.twz.cx.mcore.Simulator;
import org.twz.dag.Gene;
import org.twz.statistic.Statistics;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by TimeWz on 14/08/2018.
 */
public class ODEquationsTest {
    class SIR extends ODEquations {

        public SIR(String name, String[] y_names, double dt, double fdt, Map<String, Double> parameters) {
            super(name, y_names, dt, fdt, parameters);
        }


        @Override
        public void computeDerivatives(double v, double[] y0, double[] y1) throws MaxCountExceededException, DimensionMismatchException {
            double beta = getParameter("beta"), gamma = getParameter("gamma");
            double n = y0[0] + y0[1] + y0[2];

            y1[0] = - beta * y0[0] * y0[1] / n;
            y1[1] = beta * y0[0] * y0[1] / n - gamma * y0[1];
            y1[2] = gamma * y0[1];
        }
    }

    private EquationBasedModel EBM;
    private Map<String, Double> Pars;

    @Before
    public void setUp() throws Exception {
        Pars = new HashMap<>();
        Pars.put("beta", 1.5);
        Pars.put("gamma", 0.5);

        ODEquations Eq = new SIR("SIR", new String[]{"S", "I", "R"}, 1, 0.1, Pars);
        EBM = new EquationBasedModel("SIR", Eq, Pars, new EBMY0());
        EBM.addObservingStock("S");
        EBM.addObservingStock("I");
        EBM.addObservingStock("R");
    }

    @Test
    public void simulation() {
        Simulator Simu = new Simulator(EBM);
        Simu.addLogPath("log/EBM.txt");
        EBMY0 Y0 = new EBMY0();
        Y0.append("{'y': 'S', 'n': 990}");
        Y0.append("{'y': 'I', 'n': 10}");

        Simu.simulate(Y0, 0, 15, 1);
        EBM.getObserver().getObservations().print();
    }

}