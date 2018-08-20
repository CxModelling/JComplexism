package org.twz.cx.multimodel;


import org.twz.cx.Director;
import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.BranchY0;
import org.twz.cx.mcore.IY0;
import org.twz.cx.multimodel.entries.IModelEntry;
import org.twz.cx.multimodel.entries.MultipleEntry;
import org.twz.cx.multimodel.entries.SingleEntry;
import org.twz.dag.ParameterCore;
import org.twz.dag.util.NodeGroup;
import org.twz.dataframe.Tuple;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/7/16.
 */
public class ModelLayout {
    private final String Name;
    private List<IModelEntry> ModelEntries;
    private Map<String, ModelLayout> Children;

    public ModelLayout(String name) {
        Name = name;
        ModelEntries = new ArrayList<>();
        Children = new HashMap<>();
    }

    public String getName() {
        return Name;
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

    public void addRelation(String source, String target) {

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

    public NodeGroup getParameterHierarchy(Director da) {
        NodeGroup ng = new NodeGroup(Name, null);
        Children.values().forEach(e->ng.appendChildren(e.getParameterHierarchy(da)));
        Set<String> leaves = new HashSet<>();
        ModelEntries.forEach(e->leaves.add(e.getProtoName()));
        leaves.forEach(e->ng.appendChildren(da.getSimModel(e).getParameterHierarchy(da)));
        return ng;
    }

    public AbsSimModel generate(String name, Director da, ParameterCore pc, boolean all_observed) {
        MultiModel model = new MultiModel(name, pc);
        AbsSimModel sub;

        for (IModelEntry entry : ModelEntries) {
            List<Tuple<String, String, IY0>> ms = entry.generate();
            for (Tuple<String, String, IY0> m : ms) {
                sub = da.generateMCore(m.getFirst(), m.getSecond(), pc.breed(m.getFirst(), m.getSecond()));
                model.appendModel(sub);
                if (all_observed) model.addObservingModel(m.getFirst());
            }

        }

        return model;
    }

    public AbsSimModel generate(String name, Director da, ParameterCore pc) {
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
}
