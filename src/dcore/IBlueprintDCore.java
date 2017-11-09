package dcore;

import org.json.JSONObject;
import utils.json.AdapterJSONObject;
import pcore.ParameterCore;

/**
 *
 * Created by TimeWz on 2016/12/22.
 */
public interface IBlueprintDCore<M extends AbsDCore> extends AdapterJSONObject {
    String getName();
    boolean isCompatible(ParameterCore pc);
    String[] getRequiredDistributions();
    M generateModel(ParameterCore pc);
    void buildJSON();
}
