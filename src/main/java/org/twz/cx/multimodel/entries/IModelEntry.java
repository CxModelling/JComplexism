package org.twz.cx.multimodel.entries;

import org.twz.cx.mcore.Y0;
import org.twz.dataframe.Tuple;
import org.twz.io.AdapterJSONObject;

import java.util.Set;

/**
 * Created by TimeWz on 2017/11/14.
 */
public interface IModelEntry<T> extends AdapterJSONObject {
    Set<Tuple<String, String, Y0<T>>> generate();
}
