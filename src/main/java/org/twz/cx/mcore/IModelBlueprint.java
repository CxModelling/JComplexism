package org.twz.cx.mcore;

import org.twz.dag.util.NodeGroup;
import org.twz.statespace.IStateSpaceBlueprint;

import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/6/17.
 */
public interface IModelBlueprint<T extends AbsSimModel> {
    String getName();
    NodeGroup getParameterHierarchy(IStateSpaceBlueprint dc);
    T generate(String name, Map<String, Object> args);
    boolean isWellDefined();
}
