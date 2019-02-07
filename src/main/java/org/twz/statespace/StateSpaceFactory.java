package org.twz.statespace;

import org.json.JSONException;
import org.twz.statespace.ctbn.CTBNBlueprint;
import org.twz.statespace.ctmc.CTMCBlueprint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.exception.ScriptException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller for building dcores
 * Created by TimeWz on 2017/6/17.
 */
public class StateSpaceFactory {
    public static IStateSpaceBlueprint createFromJSON(JSONObject js) throws JSONException {
        IStateSpaceBlueprint bp;
        if (js.getString("ModelType").equals("CTBN")) {
            bp = new CTBNBlueprint(js);
        } else {
            bp = new CTMCBlueprint(js);
        }
        return bp;
    }

    public static IStateSpaceBlueprint createFromScripts(String script) throws ScriptException, JSONException {
        JSONObject js = script2json(script);
        return createFromJSON(js);
    }

    private static JSONObject script2json(String script) throws ScriptException, JSONException {
        JSONObject js = new JSONObject();

        String[] row_lines = script.split("\n");

        String pattern = "\\s*(CTBN|CTMC)\\s+(\\w+)\\s*\\{";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(row_lines[0]);

        if (m.find( )) {
            js.put("ModelName", m.group(2));
            js.put("ModelType", m.group(1));
        } else {
            throw new ScriptException("Illegal script");
        }

        List<String> lines = new LinkedList<>();
        String line;
        for (int i = 1; i < row_lines.length; i++) {
            line = row_lines[i].replaceAll("\\s+", "");
            line = line.replaceAll("#\\w*", "");
            lines.add(line);
        }

        Map<String, List<String>> targets = new HashMap<>();

        if (js.getString("ModelType").equals("CTBN")) {
            // Identify microstates
            r = Pattern.compile("(\\w+)\\[(\\S+)\\]");
            List<String> order = new ArrayList<>();
            JSONObject mss = new JSONObject(), sts = new JSONObject();
            for (String l: lines) {
                m = r.matcher(l);
                if (m.find()) {
                    order.add(m.group(1));
                    mss.put(m.group(1), new JSONArray("['"+m.group(2).replace("|", "','")+"']"));
                }
            }
            // Identify states
            r = Pattern.compile("([\\w|]+)\\{(.*)\\}");
            for (String l: lines) {
                m = r.matcher(l);
                if (m.find()) {
                    JSONObject args = new JSONObject();

                    for (String s: m.group(2).split(",")) {
                        String ss[] = s.split(":");
                        args.put(ss[0], ss[1]);
                    }

                    sts.put(m.group(1), args);
                }
            }
            js.put("Order", order);
            js.put("Microstates", mss);
            js.put("States", sts);

            for (Iterator it = sts.keys(); it.hasNext(); ) {
                Object k = it.next();
                targets.put(k.toString(), new ArrayList<>());
            }
        } else {
            // Identify states
            JSONArray sts = new JSONArray();
            r = Pattern.compile("\\A([\\w|]+)\\Z");
            for (String l: lines) {
                m = r.matcher(l);
                if (m.find()) {
                    sts.put(m.group(1));
                    targets.put(m.group(1), new ArrayList<>());
                }
            }

            js.put("States", sts);
        }

        JSONObject trs = new JSONObject();
        // Identify transitions
        r = Pattern.compile("([\\w|]+)\\(([\\w|]+)\\)->([\\w|]+)");
        for (String l: lines) {
            m = r.matcher(l.replace("[\\w|]+--", ""));
            if (m.find()) {
                trs.put(m.group(1), new JSONObject("{'Dist':"+m.group(2)+",'To':"+m.group(3)+"}"));
            }
        }
        r = Pattern.compile("([\\w|]+)->([\\w|]+)");
        for (String l: lines) {
            m = r.matcher(l.replace("[\\w|]+--", ""));
            if (m.find()) {
                trs.put(m.group(1), new JSONObject("{'Dist':"+m.group(1)+",'To':"+m.group(2)+"}"));
            }
        }
        // Identify links
        r = Pattern.compile("([\\w|]+)--([\\w|]+)");
        for (String l: lines) {
            m = r.matcher(l);
            if (m.find()) {
                targets.get(m.group(1)).add(m.group(2));
            }
        }
        js.put("Transitions", trs);
        js.put("Targets", targets);

        return js;
    }
}
