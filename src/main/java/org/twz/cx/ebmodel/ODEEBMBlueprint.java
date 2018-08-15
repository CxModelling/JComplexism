package org.twz.cx.ebmodel;

import org.twz.cx.mcore.IModelBlueprint;
import org.twz.dag.util.NodeGroup;
import org.twz.statespace.IStateSpaceBlueprint;

import java.util.Map;

public class ODEEBMBlueprint implements IModelBlueprint<EquationBasedModel> {
    private final String Name;

    public ODEEBMBlueprint(String name) {
        Name = name;

    }

    @Override
    public String getName() {
        return Name;
    }

    @Override
    public NodeGroup getParameterHierarchy(IStateSpaceBlueprint dc) {
        return null;
    }

    @Override
    public EquationBasedModel generate(String name, Map<String, Object> args) {
        return null;
    }
}
