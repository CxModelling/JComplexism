package org.twz.cx.mcore;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public class Simulator<T> {
    private AbsSimModel<T> Model;
    private double Time;
    private RequestSet Receptor;
    private boolean Record;

    public Simulator(AbsSimModel<T> model, boolean rec) {
        Model = model;
        Time = 0;
        Record = rec;
        Receptor = new RequestSet();
    }

    public Simulator(AbsSimModel<T> model) {
        this(model, true);
    }

    public void simulate(Y0<T> y0, double fr, double to, double dt) {
        Time = fr;
        Model.initialise(Time, y0);
        if (Record) {
            Model.initialiseObservations(fr);
            Model.pushObservations(fr);
        }
        update(to, dt);
    }

    public void update(double forward, double dt) {
        LinkedList<Double> ts = seq(Time, forward, dt);
        double f, t=ts.poll();
        while (!ts.isEmpty()) {
            f = t;
            t = ts.poll();
            if (Record) {
                step(f, (f+t)/2);
                Model.captureMidTermObservations(t);
                step((f+t)/2, t);
                Model.updateObservations(t);
                Model.pushObservations(t);
            } else {
                step(f, t);
            }
        }
    }

    private void step(double t, double end) {
        double tx = t, ti;
        Model.dropNext();
        Receptor.clear();
        while (tx < end) {
            Receptor.add(Model.next());
            ti = Receptor.getTime();
            if (ti > end) break;
            tx = ti;
            Model.fetch(Receptor.getRequests().stream().map(e->e.down().getValue()).collect(Collectors.toList()));
            Model.exec();
            Model.dropNext();
            Receptor.clear();
        }
        Time = end;
        Model.setTimeEnd(end);
    }

    private LinkedList<Double> seq(double fr, double to, double by) {
        LinkedList<Double> s = new LinkedList<>();
        double ti = fr;
        while(ti < to) {
            s.add(ti);
            ti += by;
        }
        s.add(to);
        return s;
    }

    public void onRecord() {
        Record = false;
    }

    public void offRecord() {
        Record = true;
    }

    public static <T> List<Map<String, Double>> simulate(AbsSimModel<T> model, Y0<T> y0,
                                                     double fr, double to, double dt,
                                                     boolean rec) {
        Simulator<T> sim = new Simulator<>(model, rec);
        sim.simulate(y0, fr, to, dt);
        return model.output();
    }

    public static <T> List<Map<String, Double>> update(AbsSimModel<T> model,
                                                   double fr, double to, double dt,
                                                   boolean rec) {
        Simulator<T> sim = new Simulator<>(model, rec);
        sim.Time = fr;
        sim.update(to, dt);
        return model.output();
    }

}
