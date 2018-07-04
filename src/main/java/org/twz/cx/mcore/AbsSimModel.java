package org.twz.cx.mcore;

import org.twz.cx.element.Request;
import org.twz.cx.element.RequestSet;
import org.twz.io.AdapterJSONObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by TimeWz on 2017/2/10.
 */
public abstract class AbsSimModel<T> implements AdapterJSONObject{
    private final String Name;
    protected AbsObserver<AbsSimModel<T>> Obs;
    private double TimeEnd;
    private final Meta Meta;
    protected RequestSet Requests;


    public AbsSimModel(String name, AbsObserver<AbsSimModel<T>> obs, Meta meta) {
        Name = name;
        Requests = new RequestSet();
        Meta = meta;
        Obs = obs;
        TimeEnd = Double.NaN;
    }

    public AbsObserver getObserver() {
        return Obs;
    }

    public void initialise(double ti, Y0<T> y0) {
        readY0(y0, ti);
        reset(ti);
        dropNext();
    }

    public void initialise(double ti) {
        reset(ti);
        dropNext();
    }

    public String getName() {
        return Name;
    }

    public Double get(String s) {
        return Obs.get(s);
    }

    public org.twz.cx.mcore.Meta getMeta() {
        return Meta;
    }

    public abstract void clear();

    public abstract void reset(double ti);

    public abstract void readY0(Y0<T> y0, double ti);

    public abstract void listen(String src_m, String src_v, String tar_p);

    public abstract void listen(Collection<String> src_m, String src_v, String tar_p);

    public void listen(String src_m, String src_v, String tar_p, String tar_sub) {
        listen(src_m, src_v, tar_p);
    }

    public void listen(Collection<String> src_m, String src_v, String tar_p, String tar_sub) {
        listen(src_m, src_v, tar_p);
    }

    public abstract boolean impulseForeign(AbsSimModel fore, double ti);

    public List<Request> next() {
        if (Requests.isEmpty()) findNext();
        return Requests.up(Name);
    }

    public double getTimeEnd() {
        return TimeEnd;
    }

    public void setTimeEnd(double timeEnd) {
        TimeEnd = timeEnd;
    }

    public double tte() {
        return Requests.getTime();
    }

    public void dropNext() {
        Requests.clear();
    }

    public abstract void findNext();

    public abstract void fetch(List<Request> rqs);

    public abstract void exec();

    public abstract void doRequest(Request req);

    public void clearOutput() {
        Obs.renew();
    }

    public void initialiseObservations(double ti) {
        Obs.initialiseObservations(this, ti);
    }

    public void updateObservations(double ti) {
        Obs.observeRoutinely(this, ti);
    }

    public void captureMidTermObservations(double ti) {
        Obs.updateAtMidTerm(this, ti);
    }

    public void pushObservations(double ti) {
        Obs.pushObservation(ti);
    }

    public Map<String, Double> getLastObservations() {
        return Obs.getLast();
    }

    public List<Map<String, Double>> output() {
        return Obs.getTimeSeries();
    }

    public void print() {
        Obs.print();
    }
}
