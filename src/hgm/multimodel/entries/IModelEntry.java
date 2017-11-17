package hgm.multimodel.entries;

import mcore.Y0;
import utils.dataframe.Tuple;
import utils.json.AdapterJSONObject;

import java.util.Set;

/**
 * Created by TimeWz on 2017/11/14.
 */
public interface IModelEntry<T> extends AdapterJSONObject {
    Set<Tuple<String, String, Y0<T>>> generate();
}
