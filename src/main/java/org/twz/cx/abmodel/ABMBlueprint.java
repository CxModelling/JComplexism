package org.twz.cx.abmodel;

import org.twz.cx.mcore.AbsSimModel;
import org.twz.cx.mcore.IMCoreBlueprint;
import org.twz.dag.util.NodeGroup;
import org.twz.statespace.IBlueprintDCore;

import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class ABMBlueprint implements IMCoreBlueprint{
    private String Name;
    private String TargetPCore;
    private String TargetDCore;

    public ABMBlueprint(String name, String pc, String dc) {
        Name = name;
        TargetPCore = pc;
        TargetDCore = dc;
    }

    public String getName() {
        return Name;
    }

    public String getTargetPCore() {
        return TargetPCore;
    }

    public String getTargetDCore() {
        return TargetDCore;
    }

    @Override
    public AbsSimModel generate(String name, Map args) {
        return null;
    }

    @Override
    public NodeGroup getParameterHierarchy(IBlueprintDCore dc) {
        return null;
    }

    @Override
    public AbsSimModel generate(String name) {
        return null;
    }
}
