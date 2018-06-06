package org.twz.cx.abmodel.trait;

import org.json.JSONArray;

import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/8/12.
 */
public interface ITrait{
    void fill(Map<String, Object> info);
    JSONArray toJSON();
}
