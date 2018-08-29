package org.twz.cx.element;

import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dataframe.Pair;

import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public class Disclosure {
    public final String What, Who;
    public final LinkedList<String> Where;
    private final JSONObject Arguments;

    public Disclosure(String what, String who, LinkedList<String> where, JSONObject js) {
        What = what;
        Who = who;
        Where = where;
        Arguments = js;
    }

    public Disclosure(String what, String who, LinkedList<String> where) {
        this(what, who, where, new JSONObject());
    }

    public Disclosure(String what, String who, String where) {
        What = what;
        Who = who;
        Where = new LinkedList<>();
        Where.add(where);
        Arguments = new JSONObject();
    }

    public void updateArguments(Map<String, Object> args) {
        for (Map.Entry<String, Object> entry : args.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            try {
                Arguments.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateArguments(JSONObject args) {
        for (String s : JSONObject.getNames(args)) {
            try {
                Arguments.put(s, args.get(s));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONObject getArguments() {
        return Arguments;
    }

    public Object get(String key) throws JSONException {
        return Arguments.get(key);
    }

    public String getString(String key) throws JSONException {
        return Arguments.getString(key);
    }

    public double getDouble(String key) throws JSONException {
        return Arguments.getDouble(key);
    }

    public boolean has(String key) {
        return Arguments.has(key);
    }

    public Disclosure upScale(String adr) {
        LinkedList<String> new_adr = new LinkedList<>(Where);
        new_adr.add(adr);
        return new Disclosure(What, Who, new_adr, Arguments);
    }

    public Disclosure siblingScale() {
        return upScale("^");
    }

    public Pair<String, Disclosure> downScale() {
        String gp = getGroup();
        LinkedList<String> new_adr = new LinkedList<>(Where);
        new_adr.pollLast();
        return new Pair<>(gp, new Disclosure(What, Who, new_adr, Arguments));
    }

    public int getDistance() {
        return Where.size();
    }

    public String getAddress() {
        return String.join("@", Where);
    }

    public String getGroup() {
        return Where.getLast();
    }

    public String getSource() {
        return Where.getFirst();
    }

    public boolean isSibling() {
        return getDistance() == 3 & Where.getLast().equals("^");
    }

    @Override
    public String toString() {
        return "Disclosure{" +
                "What='" + What + '\'' +
                ", Who='" + Who + '\'' +
                ", Where=" + getAddress() +
                '}';
    }

    public String toLog() {
        return String.format("Disclose: %s did %s in %s", Who, What, getAddress());
    }
}
