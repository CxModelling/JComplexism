package hgm.multimodel;

import mcore.*;
import org.json.JSONObject;

import java.util.*;

/**
 *
 * Created by TimeWz on 29/09/2017.
 */
public class Summariser extends LeafModel {


    private class Task {
        String Selector, Parameter, NewName;

        public Task(String selector, String parameter, String newName) {
            Selector = selector;
            Parameter = parameter;
            NewName = newName;
        }
    }


    private Clock Clock;
    private List<Task> Tasks;
    private LinkedHashMap<String, Double> Summary;

    public Summariser(String name, double dt) {
        super(name, null, null);
        Clock = new Clock(dt);
        Tasks = new ArrayList<>();
        Summary = new LinkedHashMap<>();
    }

    @Override
    public Double get(String item) {
        try {
            return Summary.get(item);
        } catch (NullPointerException e) {
            return 0.0;
        }
    }

    @Override
    public void clear() {
        Summary.clear();
    }

    @Override
    public void findNext() {
        Requests.append(new Request(Event.NullEvent, Clock.getNext()));
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    public void reset(double ti) {
        Summary = new LinkedHashMap<>();
        Clock.initialise(ti);
    }

    @Override
    public void readY0(Y0 y0, double ti) {

    }

    @Override
    public void doRequest(Request req) {
        Clock.update(req.getTime());
    }

    public void summarise(MultiModel ms, Event evt) {
        Clock.update(evt.getTime());
        readObs(ms);
        dropNext();
    }

    public void readObs(MultiModel ms) {
        Summary = new LinkedHashMap<>();
        for (Task tk: Tasks) {
            Summary.put(tk.NewName, ms.selectAll(tk.Selector).sum(tk.Parameter));
        }
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
    public void listen(Collection<String> src_m, String src_v, String tar_p) {

    }

    @Override
    public String toJSONString() {
        return null;
    }

    public LinkedHashMap<String, Double> getSummary() {
        return Summary;
    }
}
