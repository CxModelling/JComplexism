package org.twz.cx.ebmodel;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.json.JSONObject;

import org.twz.dag.Gene;

import java.util.Map;


public abstract class ODEquations extends AbsEquations implements FirstOrderDifferentialEquations {
    private double FDt;

    private FirstOrderIntegrator Integrator;

    public ODEquations(String name, String[] y_names, double dt, double fdt, Gene parameters) {
        super(name, y_names, parameters, dt);
        FDt = fdt;
        Integrator = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
    }

    public ODEquations(String name, String[] y_names, double dt, double fdt, Map<String, Double> parameters) {
        super(name, y_names, parameters, dt);
        FDt = fdt;
        Integrator = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
    }

    public ODEquations(String name, String[] y_names, double dt, double fdt) {
        super(name, y_names, dt);
        FDt = fdt;
        Integrator = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
    }

    @Override
    protected void goTo(double t0, double[] y0, double t1, double[] y1) {
        Integrator.integrate(this, t0, y0, t1, y1);
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

                n = args.has("n")? args.getInt("n"): 1;
                setY(y, getY(y) + n);

                model.disclose(String.format("add %s by %d", y, n), getName());
                break;

            case "del":
                y = (String) args.get("y");
;
                n = args.has("n")? args.getInt("n"): 1;
                n = Math.min(n, (int) Math.floor(getY(y)));
                setY(y, getY(y) - n);
                model.disclose(String.format("del %s by %d", y, n), getName());
        }
    }
}
