package org.twz.cx.multimodel;


import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.Director;
import org.twz.cx.mcore.*;
import org.twz.cx.mcore.communicator.IChecker;
import org.twz.cx.mcore.communicator.IResponse;
import org.twz.cx.multimodel.entries.IModelEntry;
import org.twz.cx.multimodel.entries.InteractionEntry;
import org.twz.cx.multimodel.entries.MultipleEntry;
import org.twz.cx.multimodel.entries.SingleEntry;
import org.twz.dag.Parameters;
import org.twz.dag.util.NodeSet;
import org.twz.dataframe.Tuple;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/7/16.
 */
public class ModelLayout {
    private final String Name;
    private List<IModelEntry> ModelEntries;
    private List<InteractionEntry> InteractionEntries;
    private Map<String, ModelLayout> Children;
    private String TimeKey;
    private FnSummary Summariser;

    public ModelLayout(String name) {
        Name = name;
        ModelEntries = new ArrayList<>();
        InteractionEntries = new ArrayList<>();
        Children = new HashMap<>();
        TimeKey = null;
    }

    public String getName() {
        return Name;
    }

    public void setTimeKey(String timeKey) {
        TimeKey = timeKey;
    }

    public void appendChild(ModelLayout ml) {
        Children.put(ml.getName(), ml);
    }

    public void addEntry(String name, String proto, IY0 y0) {
        ModelEntries.add(new SingleEntry(name, proto, y0));
    }

    public void addEntry(String prefix, String proto, IY0 y0, int from, int to, int by) {
        ModelEntries.add(new MultipleEntry(prefix, proto, y0, from, to, by));
    }

    public void addEntry(String prefix, String proto, IY0 y0, int from, int to) {
        ModelEntries.add(new MultipleEntry(prefix, proto, y0, from, to));
    }

    public void addEntry(String prefix, String proto, IY0 y0, int to) {
        ModelEntries.add(new MultipleEntry(prefix, proto, y0, to));
    }

    public void addEntry(String prefix, String proto, int to) {
        addEntry(prefix, proto, null, to);
    }

    public void addEntry(String name, String proto) {
        ModelEntries.add(new SingleEntry(name, proto, null));
    }

    public void addInteraction(String sel, String checker, String response) throws JSONException {
        InteractionEntries.add(new InteractionEntry(sel, checker, response));
    }

    public void addInteraction(String sel, JSONObject checker, JSONObject response) throws JSONException {
        InteractionEntries.add(new InteractionEntry(sel, checker, response));
    }

    public void addInteraction(JSONObject js) throws JSONException {
        InteractionEntries.add(new InteractionEntry(js));
    }

    public void addInteraction(String sel, IChecker checker, IResponse response) {
        InteractionEntries.add(new InteractionEntry(sel, checker, response));
    }

    public List<Tuple<String, String, IY0>> getModels() {
        List<Tuple<String, String, IY0>> ms = new ArrayList<>();
        for (IModelEntry entry : ModelEntries) {
            ms.addAll(entry.generate());
        }
        return ms;
    }

    public int size() {
        return ModelEntries.stream().mapToInt(IModelEntry::size).sum();
    }

    public NodeSet getParameterHierarchy(Director da) {
        NodeSet ns = new NodeSet(Name, null);
        Children.values().forEach(e->ns.appendChild(e.getParameterHierarchy(da)));
        Set<String> leaves = new HashSet<>();
        ModelEntries.forEach(e->leaves.add(e.getProtoName()));
        leaves.forEach(e->ns.appendChild(da.getSimModel(e).getParameterHierarchy(da)));
        return ns;
    }

    public AbsSimModel generate(String name, Director da, Parameters pc, boolean all_observed) {
        MultiModel model = new MultiModel(name, pc);

        AbsSimModel sub;

        for (IModelEntry entry : ModelEntries) {
            List<Tuple<String, String, IY0>> ms = entry.generate();
            for (Tuple<String, String, IY0> m : ms) {
                sub = da.generateMCore(m.getFirst(), m.getSecond(), pc.breed(m.getFirst(), m.getSecond()));
                if (TimeKey != null) sub.setTimeKey(TimeKey);
                model.appendModel(sub);
                if (all_observed) model.addObservingModel(m.getFirst());
            }

        }

        for (InteractionEntry inter : InteractionEntries) {
            for (AbsSimModel chd : model.selectAll(inter.getSelector()).values()) {
                chd.addListener(inter.passChecker(), inter.passResponse());
            }
        }

        if (Summariser != null) {
            model.getObserver().setSummariser(Summariser);
        } else {
            model.getObserver().setSummariser((tab, m, ti) -> {});
        }
        if (TimeKey != null) model.setTimeKey(TimeKey);
        return model;
    }

    public AbsSimModel generate(String name, Director da, Parameters pc) {
        return this.generate(name, da, pc, true);
    }

    public IY0 getY0s() {
        BranchY0 y0 = new BranchY0();
        for (IModelEntry entry : ModelEntries) {
            List<Tuple<String, String, IY0>> ms = entry.generate();
            for (Tuple<String, String, IY0> m : ms) {
                y0.appendChildren(m.getFirst(), m.getThird());
            }

        }
        return y0;
    }

    public void setSummariser(FnSummary summariser) {
        Summariser = summariser;
    }
}
