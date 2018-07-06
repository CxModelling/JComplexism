package org.twz.cx.mcore;

import org.twz.cx.element.Disclosure;
import org.twz.cx.element.Request;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public class Simulator {
    private AbsSimModel Model;
    private double Time;
    private boolean Record;
    private Map<String, AbsSimModel> LazyModels;

    public Simulator(AbsSimModel model, boolean rec) {
        Model = model;
        Time = 0;
        Record = rec;
        LazyModels = new HashMap<>();
    }

    public Simulator(AbsSimModel model) {
        this(model, true);
    }

    public void simulate(IY0 y0, double fr, double to, double dt) {
        Time = fr;
        Model.initialise(Time, y0);
        dealWithDisclosures(fr, null);
        if (Record) {
            Model.initialiseObservations(fr);
            Model.pushObservations(fr);
        }
        update(to, dt);
    }

    public void update(double forward, double dt) {
        dealWithDisclosures(Time, null);

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
        while (tx < end) {
            try {
                Model.collectRequests();
                List<Request> rs = Model.getScheduler().getRequests();
                ti = rs.get(0).getTime();
                if (ti > end) break;

                tx = ti;
                Model.fetchRequests(rs);
                Model.executeRequests();
                dealWithDisclosures(tx, rs);
            } catch (Exception e) {
                dealWithDisclosures(tx, null);
            } finally {
                Model.exitCycle();
            }
        }
        Model.exitCycle();
        Time = end;
        Model.setTimeEnd(end);
    }

    private void dealWithDisclosures(double ti, List<Request> requests) {
        List<Disclosure> ds;
        if (requests != null) {
            ds = requests.stream().map(Request::disclose).collect(Collectors.toList());
        } else {
            ds = new ArrayList<>();
        }

        ds.addAll(Model.collectDisclosure());
        ds = ds.stream().filter(d -> !d.getSource().equals(Model.getName())).collect(Collectors.toList());

        Map<Disclosure, AbsSimModel> dm;
        while (!ds.isEmpty()) {
            dm = new HashMap<>();
            for (Disclosure d: ds) {
                dm.put(d.downScale().getSecond(), findModel(d));
            }
            Model.fetchDisclosures(dm, ti);
            ds = Model.collectDisclosure();
            ds = ds.stream().filter(d -> !d.getSource().equals(Model.getName())).collect(Collectors.toList());
        }
    }

    private AbsSimModel findModel(Disclosure dis) {
        String adr = dis.getAddress();
        try {
            return LazyModels.get(adr);
        } catch (NullPointerException e) {
            AbsSimModel m = Model;
            List<String> where = new LinkedList<>(dis.Where);
            for (int i = where.size() - 2; i >= 0; i--) {
                m = ((BranchModel) m).getModel(where.get(i));
            }
            return m;
        }
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

    public static List<Map<String, Double>> simulate(AbsSimModel model, IY0 y0,
                                                     double fr, double to, double dt,
                                                     boolean rec) {
        Simulator sim = new Simulator(model, rec);
        sim.simulate(y0, fr, to, dt);
        return model.output();
    }

    public static List<Map<String, Double>> update(AbsSimModel model,
                                                   double fr, double to, double dt,
                                                   boolean rec) {
        Simulator sim = new Simulator(model, rec);
        sim.Time = fr;
        sim.update(to, dt);
        return model.output();
    }

}
