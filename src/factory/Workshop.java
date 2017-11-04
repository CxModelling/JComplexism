package factory;

import factory.arguments.AbsArgument;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Workshop is a builder object which can build various object with (name, args)
 * Created by TimeWz on 2017/11/3.
 */
public class Workshop {
    private static Map<String, Workshop> Workshops = new HashMap<>();
    public static Workshop getWorkshop(String ws) {
        if (Workshops.containsKey(ws)) {
            return Workshops.get(ws);
        } else {
            Workshop w = new Workshop();
            Workshops.put(ws, w);
            return w;
        }
    }


    private Map<String, Object> Resources;
    private Map<String, Creator> Creators;

    private Workshop() {
        Resources = new HashMap<>();
        Creators = new HashMap<>();
    }

    public Object getResource(String name) {
        return Resources.get(name);
    }

    public void clearResource() {
        Resources.clear();
    }

    public void appendResource(String name, Object resource) {
        Resources.put(name, resource);
    }

    public <T> void register(String tp, Class<T> cls, AbsArgument[] args) {
        Creator<T> cr = new Creator<>(tp, cls, args);
        Creators.put(tp, cr);
    }

    public Object create(JSONObject js) throws InstantiationError {
        String name = js.getString("Name");
        String type = js.getString("Type");
        JSONObject args = js.getJSONObject("Args");
        return create(name, type, args);
    }

    public Object create(String name, String type, JSONObject args) throws InstantiationError {
        Creator cr = Creators.get(type);
        return cr.create(name, args, this);
    }

    public void listCreators() {
        for (String cr: Creators.keySet()) {
            System.out.println(cr);
        }
    }
}
