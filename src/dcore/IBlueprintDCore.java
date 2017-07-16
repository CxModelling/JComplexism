package dcore;

import org.json.JSONObject;
import pcore.ParameterCore;

/**
 *
 * Created by TimeWz on 2016/12/22.
 */
public interface IBlueprintDCore<M extends AbsDCore> {
    String getName();
    boolean isCompatible(ParameterCore pc);
    M generateModel(ParameterCore pc);
    JSONObject toJSON();
}
