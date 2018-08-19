package org.twz.cx.multimodel.entries;

import org.twz.cx.mcore.IY0;
import org.twz.dataframe.Tuple;
import org.twz.io.AdapterJSONObject;

import java.util.List;
import java.util.Set;


/**
 * Created by TimeWz on 2017/11/14.
 */
public interface IModelEntry extends AdapterJSONObject {
    List<Tuple<String, String, IY0>> generate();
    int size();
}
