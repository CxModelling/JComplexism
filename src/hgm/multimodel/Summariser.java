package hgm.multimodel;


import mcore.Ticker.ClockTicker;
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

        Task(String selector, String parameter, String newName) {
            Selector = selector;
            Parameter = parameter;
            NewName = newName;
        }
    }

    private ClockTicker Clock;
    private List<Task> Tasks;
    private List<String> ToBeSummarised;
    private double LastObservation;
    private ModelSet MM;
    private LinkedHashMap<String, Double> Impulses;
    private LinkedHashMap<String, Double> Summary;

    Summariser(String name, double dt) {
        super(name, null, null);
        Clock = new ClockTicker(dt);
        Tasks = new ArrayList<>();
        ToBeSummarised = new ArrayList<>();
        Summary = new LinkedHashMap<>();
        Impulses = new LinkedHashMap<>();
        LastObservation = -1;
    }

    public void setModel(ModelSet mm) {
        MM = mm;
    }

    void addObsModel(String selector) {
        ToBeSummarised.add(selector);
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
        Summary.clear();
    }

    @Override
    public void findNext() {
        Requests.append(new Request(Event.NullEvent, Clock.getNext()));
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
        readTasks();
    }

    void readTasks() {
        for (Task tk: Tasks) {
            Summary.put(tk.NewName, MM.selectAll(tk.Selector).sum(tk.Parameter));
        }
    }

    LinkedHashMap<String, Double> getImpulses() {
        return Impulses;
    }

    void summarise(double time) {
        Map<String, Double> obs;
        String k;
        if (time == LastObservation && !Summary.isEmpty()) {
            return;
        }
        for (String sel: ToBeSummarised) {
            obs = MM.selectAll(sel).sum();
            for (Map.Entry<String, Double> ent: obs.entrySet()) {
                if (ent.getKey().equals("Time")) continue;
                k = (sel.equals("*")? "": sel+"@") + ent.getKey();
                Summary.putIfAbsent(k, ent.getValue());
            }
        }
        Summary.put("Time", time);
        LastObservation = time;
    }

    LinkedHashMap<String, Double> getSummary() {
        return Summary;
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
        // todo
        return null;
    }

}
