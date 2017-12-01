package hgm.multimodel;

import hgm.multimodel.entries.RelationEntry;
import mcore.*;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.json.JSONObject;

import java.util.*;

/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class ModelSet extends BranchModel{
    private Summariser Summariser;
    private Map<String, Set<String>> Networks;

    public ModelSet(String name, mcore.Meta meta, double dt) {
        super(name, new ObsMM(), meta);
        Summariser = new Summariser(name, dt);
        Networks = new HashMap<>();
        Networks.put(name, new HashSet<>());
        Summariser.setModel(this);
    }

    public void addObsModel(String sel) {
        if (selectAll(sel).size() > 0) {
            ((ObsMM) getObserver()).addObservedSelector(sel);
        }
    }

    @Override
    public void clear() {
        Models.values().forEach(AbsSimModel::clear);
        Summariser.clear();
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
    public boolean impulseForeign(AbsSimModel fore, double ti) {
        // todo multilevel models
        return false;
    }


    public void link(String src, String tar) {
        link(new RelationEntry(src), new RelationEntry(tar));
    }

    public void link(RelationEntry src, RelationEntry tar) {
        ModelSelector msTar, msSrc;

        if (src.Selector.equals(getName())) {
            msTar = selectAll(tar.Selector);
            for (Map.Entry<String, AbsSimModel> eT: msTar.entrySet()) {
                eT.getValue().listen(src.Selector, src.Parameter, tar.Parameter);
                buildEdge(getName(), eT.getKey());
            }
            return;
        }
        if (tar.Selector.equals(getName())) {
            msSrc = selectAll(tar.Selector);
            Summariser.listen(src.Selector, src.Parameter, tar.Parameter);

            for (String kS: msSrc.keySet()) {
                buildEdge(kS, getName());
            }
            return;
        }

        msTar = selectAll(tar.Selector);

        if (src.isSingle()) {
            for (Map.Entry<String, AbsSimModel> eT: msTar.entrySet()) {
                eT.getValue().listen(src.Selector, src.Parameter, tar.Parameter);
                buildEdge(src.Selector, eT.getKey());
            }
        } else {
            msSrc = selectAll(src.Selector);
            Set<String> ms = msSrc.keySet();
            for (Map.Entry<String, AbsSimModel> eT: msTar.entrySet()) {
                eT.getValue().listen(ms, src.Parameter, tar.Parameter);
                buildEdge(src.Selector, eT.getKey());
            }
        }
    }

    private void buildEdge(String src, String tar) {
        if (src.equals(tar)) return;
        try {
            Networks.get(src).add(tar);
        } catch (NullPointerException e) {
            Set<String> col = new HashSet<>();
            col.add(src);
            Networks.put(tar, col);
        }
    }

    @Override
    public void findNext() {
        for (Map.Entry<String, AbsSimModel> ent: Models.entrySet()) {
            for (Object req: ent.getValue().next()) {
                Requests.append(((Request) req));
            }
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
            crossImpulse(ti);
            Summariser.doRequest(req);
            Summariser.dropNext();
        }
    }

    @Override
    public void pushObservations(double ti) {
        for (AbsSimModel mod: Models.values()) {
            mod.pushObservations(ti);
        }
        super.pushObservations(ti);
    }

    @Override
    public void clearOutput() {
        Models.values().forEach(AbsSimModel::clearOutput);
        super.clearOutput();
    }

    @Override
    public void initialiseObservations(double ti) {
        Models.values().forEach(m->m.initialiseObservations(ti));
        super.initialiseObservations(ti);
    }

    @Override
    public void updateObservations(double ti) {
        Models.values().forEach(m->m.updateObservations(ti));
        super.updateObservations(ti);
    }


    @Override
    public void captureMidTermObservations(double ti) {
        Models.values().forEach(m->m.captureMidTermObservations(ti));
        super.captureMidTermObservations(ti);
    }

    Summariser getSummariser() {
        return Summariser;
    }

    private void crossImpulse(double ti) {
        //todo
        Summariser.readTasks();
        AbsSimModel ms;
        for (Map.Entry<String, Set<String>> ent: Networks.entrySet()) {
            ms = (ent.getKey().equals(getName()))? Summariser: getModel(ent.getKey());
            for (String v: ent.getValue()) {
                if (!v.equals(ent.getKey())) {
                    getModel(v).impulseForeign(ms, ti);
                }
            }
        }

    }
}
