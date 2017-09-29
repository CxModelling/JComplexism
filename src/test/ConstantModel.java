package test;

import mcore.*;
import org.json.JSONObject;

import java.util.Collection;

/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class ConstantModel extends LeafModel {

    private Clock Timer;

    public ConstantModel(String name, mcore.Meta meta, double dt) {
        super(name, new AbsObserver() {
            @Override
            public void initialiseObservation(AbsSimModel model, double ti) {

            }

            @Override
            public void updateObservation(AbsSimModel model, double ti) {

            }
        }, meta);
        Timer = new Clock(0, 0, dt);
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
    public void findNext() {
        double ti = Timer.getNext();
        Event evt = new Event(getName(), ti);
        Requests.appendSRC("Step", evt, ti);
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    public void doRequest(Request req) {
        Timer.update(req.getTime());
        System.out.println(req.getEvent());

    }

    @Override
    public String toJSONString() {
        return null;
    }
}
