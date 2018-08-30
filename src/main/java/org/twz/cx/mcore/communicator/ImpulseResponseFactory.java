package org.twz.cx.mcore.communicator;

import org.json.JSONException;
import org.json.JSONObject;

public class ImpulseResponseFactory {
    public static IChecker getChecker(JSONObject js) throws JSONException {
        String type = js.getString("Type");
        switch (type) {
            case "IsChecker":
                return new IsChecker(js);
            case "StartWithChecker":
                return new StartWithChecker(js);
            case "WhoStartWithChecker":
                return new WhoStartWithChecker(js);
            case "InitialChecker":
                return new InitialChecker();
            case "InclusionChecker":
                return new InclusionChecker(js);
            case "HaveStringChecker":
                return new HaveStringChecker(js);
            case "SiblingChecker":
                return new SiblingChecker(js);
        }
        throw new JSONException("Unknown type of checker");
    }

    public static IResponse getResponse(JSONObject js) throws JSONException {
        String type = js.getString("Type");
        switch (type) {
            case "ValueImpulseResponse":
                return new ValueImpulseResponse(js);
            case "AddOneResponse":
                return new AddOneResponse(js);
            case "AddNResponse":
                return new AddNResponse(js);
            case "DelOneResponse":
                return new DelOneResponse(js);
            case "DelNResponse":
                return new DelNResponse(js);
            case "ValueShockResponse":
                return new ValueShockResponse(js);
            case "MultiValueShockResponse":
                return new MultiValueShockResponse(js);
        }
        throw new JSONException("Unknown type of response");
    }

}
