package org.twz.cx.multimodel;


import org.twz.cx.element.Event;
import org.twz.cx.element.Ticker.StepTicker;
import org.twz.cx.mcore.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.twz.io.AdapterJSONObject;

import java.util.*;


/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class Summariser extends ModelAtom {

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

    private StepTicker Clock;
    private List<Task> Tasks;
    private BranchModel MM;
    private LinkedHashMap<String, Double> Impulses;

    Summariser(String name, double dt) {
        super(name);
        Clock = new StepTicker(dt);
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

    public void setModel(BranchModel mm) {
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
    public Event findNext() {
        return new Event("summarise", Clock.getNext());
    }

    @Override
    public void updateTo(double ti) {
        Clock.update(ti);
    }

    @Override
    public void executeEvent() {
        readTasks();
    }

    @Override
    public void initialise(double ti, AbsSimModel model) {
        readTasks();
    }

    @Override
    public void reset(double ti, AbsSimModel model) {
        Impulses = new LinkedHashMap<>();
        Clock.initialise(ti);
    }

    @Override
    public void shock(double ti, Object source, String target, Object value) {

    }

    void readTasks() {
        for (Task tk: Tasks) {
            Impulses.put(tk.NewName, MM.selectAll(tk.Selector).sum(tk.Parameter));
        }
    }

    LinkedHashMap<String, Double> getImpulses() {
        return Impulses;
    }

    public void listen(String src_model, String src_value, String par_tar) {
        if (par_tar == null) {
            if (src_model.equals("*")) {
                par_tar = src_value;
            } else {
                par_tar = src_model + "." + src_value;
            }
        }
        Tasks.add(new Task(src_model, src_value, par_tar));
    }

    @Override
    public JSONObject toJSON() {
        JSONObject js = new JSONObject();
        js.put("Tasks", Tasks.stream().map(Task::toJSON));
        js.put("Timer",  Clock.toJSON());
        return null;
    }

}
