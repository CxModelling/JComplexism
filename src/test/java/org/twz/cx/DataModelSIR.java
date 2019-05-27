package org.twz.cx;

import org.json.JSONException;
import org.twz.cx.ebmodel.AbsEquations;
import org.twz.cx.ebmodel.EBMY0;
import org.twz.cx.ebmodel.EquationBasedModel;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IY0;
import org.twz.dag.Chromosome;


public class DataModelSIR extends DataModel {

    public DataModelSIR(Director ctrl, String bn, String simModel, double t0, double t1, double dt,
                        String warm_up, double t_warm_up) {
        super(ctrl, bn, simModel, t0, t1, dt, warm_up, t_warm_up);
    }

    @Override
    protected IY0 sampleY0(Chromosome chromosome) {
        EBMY0 y0 = new EBMY0();
        try {
            y0.append("{'y': 'Sus', 'n': 990}");
            y0.append("{'y': 'Inf', 'n': 10}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return y0;
    }

    @Override
    protected boolean checkMidTerm(IY0 y0, Chromosome chromosome) {
        return y0.getEntries().stream().anyMatch(e-> {
            try {
                return e.getString("y").equals("Inf") && e.getDouble("n") > 0;
            } catch (JSONException ex) {
                return false;
            }
        });
    }

    @Override
    protected IY0 transportY0(AbsSimModel model) {
        AbsEquations Eq = ((EquationBasedModel)model).getEquations();

        EBMY0 y0 = new EBMY0();
        try {
            y0.append("{'y': 'Sus', 'n': "+Eq.getY("Sus")+"}");
            y0.append("{'y': 'Inf', 'n': "+Eq.getY("Inf")+"}");
            y0.append("{'y': 'Rec', 'n': "+Eq.getY("Rec")+"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return y0;
    }

}
