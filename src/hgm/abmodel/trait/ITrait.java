package hgm.abmodel.trait;

import org.json.JSONArray;
import org.json.JSONString;

import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/8/12.
 */
public interface ITrait{
    void fill(Map<String, Object> info);
    JSONArray toJSON();
}
