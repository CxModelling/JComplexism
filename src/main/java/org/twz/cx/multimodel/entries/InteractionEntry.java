package org.twz.cx.multimodel.entries;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.cx.mcore.communicator.*;
import org.twz.io.AdapterJSONObject;

public class InteractionEntry implements AdapterJSONObject {
    private final String Selector;
    private IChecker Checker;
    private IResponse Response;

    public InteractionEntry(String selector, JSONObject checker, JSONObject response) throws JSONException {
        this(selector, ImpulseResponseFactory.getChecker(checker),
                ImpulseResponseFactory.getResponse(response));
    }

    public InteractionEntry(String selector, String checker, String response) throws JSONException {
        this(selector, new JSONObject(checker), new JSONObject(response));
    }


    public InteractionEntry(String selector, IChecker checker, IResponse response) {
        Selector = selector;
        Checker = checker;
        Response = response;
    }

    public InteractionEntry(JSONObject js) throws JSONException {
        this(js.getString("Selector"),
                js.getJSONObject("Checker"),
                js.getJSONObject("Response"));
    }

    public IChecker passChecker() {
        if (Checker instanceof AbsChecker) {
            return ((AbsChecker) Checker).deepcopy();
        } else {
            return Checker;
        }
    }

    public IResponse passResponse() {
        if (Response instanceof AbsResponse) {
            return ((AbsResponse) Response).deepcopy();
        } else {
            return Response;
        }
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = new JSONObject();
        js.put("Selector", Selector);
        js.put("Checker", ((AbsChecker) Checker).toJSON());
        js.put("Response", ((AbsResponse) Response).toJSON());
        return js;
    }

    @Override
    public String toString() {
        return "Interaction{" +
                "Selector='" + Selector + '\'' +
                ", Checker=d" + Checker +
                ", Response=" + Response +
                '}';
    }

    protected InteractionEntry deepcopy() throws JSONException {
        return new InteractionEntry(this.toJSON());
    }
}
