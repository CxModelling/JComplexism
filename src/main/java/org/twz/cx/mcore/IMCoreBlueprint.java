package org.twz.cx.mcore;

import org.twz.dag.util.NodeGroup;
import org.twz.statespace.IBlueprintDCore;

import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/6/17.
 */
public interface IMCoreBlueprint<T extends AbsSimModel> {
    NodeGroup getParameterHierarchy(IBlueprintDCore dc);
    T generate(String name, Map<String, Object> args);
    T generate(String name);
}
