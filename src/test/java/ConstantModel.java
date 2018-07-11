import org.twz.cx.element.Event;
import org.twz.cx.element.Request;
import org.twz.cx.element.Ticker.StepTicker;
import org.twz.cx.mcore.*;
import org.json.JSONObject;

import java.util.Map;

/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class ConstantModel extends LeafModel {
    private static class ObsCM extends AbsObserver<ConstantModel> {
        @Override
        protected void readStatics(ConstantModel model, Map<String, Double> tab, double ti) {
            tab.put("A", model.getTimeEnd());
        }

        @Override
        public void updateDynamicObservations(ConstantModel model, Map<String, Double> flows, double ti) {

        }
    }

    private StepTicker Timer;


    public ConstantModel(String name, double dt) {
        super(name, null, new ObsCM(), null);
        Timer = new StepTicker("", dt);
    }


    @Override
    public void reset(double ti) {
        Timer.initialise(ti);
    }

    @Override
    public void readY0(IY0 y0, double ti) {

    }

    @Override
    public void findNext() {
        double ti = Timer.getNext();
        Event evt = new Event("step", ti);
        request(evt, getName());
    }

    @Override
    public void doRequest(Request req) {
        Timer.update(req.getTime());
        System.out.println(req.Todo);
    }

    @Override
    public void validateRequests() {

    }

    @Override
    public void addListener(IEventListener listener) {

    }

    @Override
    public Double getSnapshot(String key, double ti) {
        return null;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }
}
