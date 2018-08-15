package org.twz.cx.abmodel;

import org.twz.cx.mcore.IModelBlueprint;
import org.twz.dag.util.NodeGroup;
import org.twz.statespace.IStateSpaceBlueprint;

import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/6/16.
 */
public class ABMBlueprint implements IModelBlueprint<AbsAgentBasedModel> {
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
    public AbsAgentBasedModel generate(String name, Map args) {
        return null;
    }

    @Override
    public NodeGroup getParameterHierarchy(IStateSpaceBlueprint dc) {
        return null;
    }

}
