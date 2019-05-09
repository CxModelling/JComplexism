package org.twz.cx.abmodel.network;


import org.json.JSONObject;
import org.twz.factory.Workshop;
import org.twz.factory.arguments.AbsArgument;

public class NetworkFactory {
    private static Workshop<AbsNetwork> Factory = new Workshop<>();

    public static void register(String obj, Class<? extends AbsNetwork> cls, AbsArgument[] ags) {
        Factory.register(obj, cls, ags);
    }

    public static void appendResource(String key, Object res) {
        Factory.appendResource(key, res);
    }

    public static void clearResource() {
        Factory.clearResource();
    }

    public static AbsNetwork create(JSONObject js) {
        return Factory.create(js);
    }
}
