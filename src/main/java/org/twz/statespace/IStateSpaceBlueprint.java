package org.twz.statespace;

import org.json.JSONException;
import org.twz.dag.Parameters;
import org.twz.io.AdapterJSONObject;

/**
 *
 * Created by TimeWz on 2016/12/22.
 */
public interface IStateSpaceBlueprint<M extends AbsStateSpace> extends AdapterJSONObject {
    String getName();
    boolean isCompatible(Parameters pc);
    String[] getRequiredDistributions();
    M generateModel(Parameters pc);
    void buildJSON() throws JSONException;
}
