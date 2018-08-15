package org.twz.cx.element;

import org.json.JSONObject;
import org.twz.dataframe.Pair;

import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public class Disclosure {
    public final String What, Who;
    public final LinkedList<String> Where;
    private final JSONObject Arguments;

    public Disclosure(String what, String who, LinkedList<String> where) {
        What = what;
        Who = who;
        Where = where;
        Arguments = new JSONObject();
    }

    public Disclosure(String what, String who, String where) {
        What = what;
        Who = who;
        Where = new LinkedList<>();
        Where.add(where);
        Arguments = new JSONObject();
    }

    public void updateArguments(Map<String, Object> args) {
        args.forEach(Arguments::put);
    }

    public void updateArguments(JSONObject args) {
        for (String s : JSONObject.getNames(args)) {
            args.put(s, args.get(s));
        }
    }

    public JSONObject getArguments() {
        return Arguments;
    }

    public Object get(String key) {
        return Arguments.get(key);
    }

    public String getString(String key) {
        return Arguments.getString(key);
    }

    public double getDouble(String key) {
        return Arguments.getDouble(key);
    }

    public boolean has(String key) {
        return Arguments.has(key);
    }

    public Disclosure upScale(String adr) {
        LinkedList<String> new_adr = new LinkedList<>(Where);
        new_adr.add(adr);
        return new Disclosure(What, Who, new_adr);
    }

    public Disclosure siblingScale() {
        return upScale("^");
    }

    public Pair<String, Disclosure> downScale() {
        String gp = getGroup();
        LinkedList<String> new_adr = new LinkedList<>(Where);
        new_adr.pollLast();
        return new Pair<>(gp, new Disclosure(What, Who, new_adr));
    }

    public int getDistance() {
        return Where.size();
    }

    public String getAddress() {
        return Where.stream().collect(Collectors.joining("@"));
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
        return "Disclose: " +
                Who + " did " + What + " in " + getAddress();
    }
}
