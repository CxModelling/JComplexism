package dcore;

import org.json.JSONObject;
import pcore.ParameterCore;

/**
 *
 * Created by TimeWz on 2016/12/22.
 */
public interface IBlueprint<M extends AbsDynamicModel> {
    boolean isCompatible(ParameterCore pc);
    M generateModel(ParameterCore pc, String mn);
    JSONObject toJSON();
}
