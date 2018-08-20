package org.twz.cx.multimodel;

import org.json.JSONArray;
import org.json.JSONObject;
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
    private BranchY0 Y0;

    public ModelLayout(String name, BranchY0 y0) {
        Name = name;
        ModelEntries = new ArrayList<>();
        Children = new HashMap<>();
        Y0 = y0;
    }

    public ModelLayout(String name) {
        this(name, new BranchY0());
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
        return ng;
    }

    public AbsSimModel generate(Director da, ParameterCore pc) {


        return null; // todo
    }
}
