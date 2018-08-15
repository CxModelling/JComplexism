package org.twz.statespace;

import org.twz.io.AdapterJSONObject;
import org.twz.dag.ParameterCore;

/**
 *
 * Created by TimeWz on 2016/12/22.
 */
public interface IStateSpaceBlueprint<M extends AbsStateSpace> extends AdapterJSONObject {
    String getName();
    boolean isCompatible(ParameterCore pc);
    String[] getRequiredDistributions();
    M generateModel(ParameterCore pc);
    void buildJSON();
}
