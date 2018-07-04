package org.twz.cx.multimodel;


import org.twz.cx.element.Event;
import org.twz.cx.element.Request;
import org.twz.cx.mcore.Ticker.ClockTicker;
import org.twz.cx.mcore.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.io.AdapterJSONObject;

import java.util.*;


/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class Summariser extends LeafModel {

    private class Task implements AdapterJSONObject {
        String Selector, Parameter, NewName;

        Task(String selector, String parameter, String newName) {
            Selector = selector;
            Parameter = parameter;
            NewName = newName;
        }

        Task(JSONObject js) {
            this(js.getString("Selector"), js.getString("Parameter"), js.getString("NewName"));
        }

        @Override
        public JSONObject toJSON() {
            return new JSONObject("{'Selector': "+ Selector +
                    ", 'Parameter': "+ Parameter + ", 'NewName': " + NewName + "}");
        }
    }

    private ClockTicker Clock;
    private List<Task> Tasks;
    private ModelSet MM;
    private LinkedHashMap<String, Double> Impulses;

    Summariser(String name, double dt) {
        super(name, null, null);
        Clock = new ClockTicker(dt);
        Tasks = new ArrayList<>();
        Impulses = new LinkedHashMap<>();
    }

    Summariser(String name, JSONObject js) {
        this(name, js.getJSONObject("Timer").getJSONObject("Args").getDouble("dt"));
        JSONArray tasks = js.getJSONArray("Tasks");

        for (int i = 0; i < tasks.length(); i++) {
            Tasks.add(new Task(tasks.getJSONObject(i)));
        }
    }

    public void setModel(ModelSet mm) {
        MM = mm;
    }

    @Override
    public Double get(String item) {
        try {
            return Impulses.get(item);
        } catch (NullPointerException e) {
            return 0.0;
        }
    }

    @Override
    public void clear() {
        Impulses.clear();
    }

    @Override
    public void findNext() {
        Requests.append(new Request(Event.NullEvent, "Summary", "*"));
    }

    @Override
    public void reset(double ti) {
        Impulses = new LinkedHashMap<>();
        Clock.initialise(ti);
    }

    @Override
    public void readY0(Y0 y0, double ti) {

    }

    @Override
    public void doRequest(Request req) {
        Clock.update(req.getTime());
        readTasks();
    }

    void readTasks() {
        for (Task tk: Tasks) {
            Impulses.put(tk.NewName, MM.selectAll(tk.Selector).sum(tk.Parameter));
        }
    }

    LinkedHashMap<String, Double> getImpulses() {
        return Impulses;
    }

    @Override
    public void listen(String src_model, String src_value, String par_tar) {
        if (par_tar == null) {
            if (src_model.equals("*")) {
                par_tar = src_value;
            } else {
                par_tar = src_model + "@" + src_value;
            }
        }
        Tasks.add(new Task(src_model, src_value, par_tar));
    }

    @Override
    public void listen(Collection<String> src_m, String src_v, String par_tar) {

    }

    @Override
    public boolean impulseForeign(AbsSimModel fore, double ti) {
        return false;
    }


    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("Tasks", Tasks.stream().map(Task::toJSON));
        js.put("Timer",  Clock.toJSON());
        return null;
    }

}
