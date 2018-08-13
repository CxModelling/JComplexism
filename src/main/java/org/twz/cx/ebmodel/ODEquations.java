package org.twz.cx.ebmodel;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.json.JSONObject;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.dag.Gene;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ODEquations extends AbsEquations implements FirstOrderDifferentialEquations {
    private final String[] YNames;
    private double[] Ys;
    private Map<String, Integer> YIndices;
    private double FDt, Last;

    private FirstOrderIntegrator Integrator;

    public ODEquations(String name, String[] y_names, double dt, double fdt, Gene parameters) {
        super(name, parameters, dt);
        YNames = y_names;
        FDt = fdt;
        Last = 0;

        YIndices = new HashMap<>();
        for (int i = 0; i < YNames.length; i++) {
            YIndices.put(YNames[i], i);
        }
        Integrator = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
    }

    public ODEquations(String name, String[] y_names, double dt, double fdt, Map<String, Double> parameters) {
        super(name, parameters, dt);
        YNames = y_names;
        FDt = fdt;
        Last = 0;

        YIndices = new HashMap<>();
        for (int i = 0; i < YNames.length; i++) {
            YIndices.put(YNames[i], i);
        }
        Integrator = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
    }

    public ODEquations(String name, String[] y_names, double dt, double fdt) {
        super(name, dt);
        YNames = y_names;
        FDt = fdt;
        Last = 0;

        YIndices = new HashMap<>();
        for (int i = 0; i < YNames.length; i++) {
            YIndices.put(YNames[i], i);
        }
        Integrator = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
    }

    @Override
    public int getDimension() {
        return YNames.length;
    }

    @Override
    public void goTo(double ti) {
        double t0 = Last;
        if (t0 == ti) return;
        Integrator.integrate(this, Last, Ys, ti, Ys);
        Last = ti;
    }

    @Override
    public void setY(double[] y) {
        System.arraycopy(y, 0, Ys, 0, getDimension());
    }

    @Override
    public void setY(Map<String, Double> y) {
        y.forEach((k, v)->Ys[YIndices.get(k)] = v);
    }

    @Override
    public Map<String, Double> getDictY() {
        return YIndices.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e->Ys[e.getValue()]));
    }

    @Override
    public void initialise(double ti, AbsSimModel model) {
        Last = ti;
    }

    @Override
    public void reset(double ti, AbsSimModel model) {
        Last = ti;
    }

    @Override
    public void shock(double ti, Object source, String target, Object value) {
        JSONObject args = (JSONObject) value;
        EquationBasedModel model = (EquationBasedModel) source;
        int n;
        String y;
        switch (target) {
            case "impulse":
                String k = args.getString("k");
                double v1 = args.getDouble("v"), v0 = args.getDouble("v");
                put(k, v1);
                model.disclose(String.format("change %s from %.4f to %.4f", k, v1, v0), getName());
                break;

            case "add":
                y = (String) args.get("y");
                assert YIndices.containsKey(y);
                n = args.has("n")? args.getInt("n"): 1;
                Ys[YIndices.get(y)] += n;
                model.disclose(String.format("add %s by %d", y, n), getName());
                break;

            case "del":
                y = (String) args.get("y");
                assert YIndices.containsKey(y);
                n = args.has("n")? args.getInt("n"): 1;
                n = Math.min(n, (int) Ys[YIndices.get(y)]);
                Ys[YIndices.get(y)] -= n;
                model.disclose(String.format("del %s by %d", y, n), getName());
        }
    }
}
