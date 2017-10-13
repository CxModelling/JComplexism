package hgm.multimodel;

import mcore.*;
import org.json.JSONObject;

import java.util.*;

/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class MultiModel extends BranchModel{
    private Summariser Summariser;
    private Map<String, Set<String>> Networks;

    public MultiModel(String name, mcore.Meta meta, double dt) {
        super(name, new ObsMM(), meta);
        Summariser = new Summariser(name, dt);
        Networks = new HashMap<>();
        Networks.put(name, new HashSet<>());
        ((ObsMM) Obs).setModels(Models);
    }

    @Override
    public void clear() {

    }

    @Override
    public void reset(double ti) {
        for (AbsSimModel mod: Models.values()) {
            mod.reset(ti);
        }
        Summariser.reset(ti);
    }

    @Override
    public void listen(String src_m, String src_v, String tar_p) {

    }

    @Override
    public void listen(Collection<String> src_m, String src_v, String tar_p) {

    }

    @Override
    public void findNext() {
        for (Map.Entry<String, AbsSimModel> ent: Models.entrySet()) {
            Requests.add(ent.getValue().next());
        }
        Summariser.findNext();
        Requests.appendSRC("Summary", null, Summariser.tte());
    }

    @Override
    public JSONObject toJSON() {

        // todo
        return null;
    }

    @Override
    public void doRequest(Request req) {
        if (req.getNode().equals("Summary")) {
            double ti = req.getTime();
            updateObservation(ti);
            crossImpulse(ti);
            updateObservation(ti);
            Summariser.dropNext();
            Summariser.doRequest(req);
        }
    }

    @Override
    public void pushObservation(double ti) {
        for (AbsSimModel mod: Models.values()) {
            mod.pushObservation(ti);
        }
        super.pushObservation(ti);
    }

    public hgm.multimodel.Summariser getSummariser() {
        return Summariser;
    }

    public void crossImpulse(double ti) {

    }
}
