package test;

import mcore.Ticker.ClockTicker;
import mcore.*;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class ConstantModel extends LeafModel {

    private ClockTicker Timer;

    public ConstantModel(String name, mcore.Meta meta, double dt) {
        super(name, new AbsObserver<ConstantModel>() {

            @Override
            public void updateDynamicObservations(ConstantModel model, Map<String, Double> flows, double ti) {

            }

            @Override
            protected void readStatics(ConstantModel model, Map<String, Double> tab, double ti) {
                tab.put("A", ti);
            }
        }, meta);
        Timer = new ClockTicker("", dt);
    }

    @Override
    public void clear() {

    }

    @Override
    public void reset(double ti) {
        Timer.initialise(ti);
    }

    @Override
    public void readY0(Y0 y0, double ti) {

    }

    @Override
    public void listen(String src_m, String src_v, String tar_p) {

    }

    @Override
    public void listen(Collection<String> src_m, String src_v, String tar_p) {

    }

    @Override
    public boolean impulseForeign(AbsSimModel fore, double ti) {
        return false;
    }

    @Override
    public void findNext() {
        double ti = Timer.getNext();
        Event evt = new Event(getName(), ti);
        Requests.appendSRC("Step", evt, ti);
    }

    @Override
    public void doRequest(Request req) {
        Timer.update(req.getTime());
        System.out.println(req.getEvent());

    }

    @Override
    public JSONObject toJSON() {
        return null;
    }
}
