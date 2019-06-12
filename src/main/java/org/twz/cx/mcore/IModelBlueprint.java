package org.twz.cx.mcore;

import org.twz.cx.Director;
import org.twz.dag.util.NodeGroup;
import org.twz.dag.util.NodeSet;

import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/6/17.
 */
public interface IModelBlueprint<T extends AbsSimModel> {
    String getName();
    void setOption(String opt, Object value);
    void setTimeKey(String k);
    NodeSet getParameterHierarchy(Director da);
    T generate(String name, Map<String, Object> args);
    boolean isWellDefined();

}
