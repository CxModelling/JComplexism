package org.twz.dag.loci;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.Chromosome;
import org.twz.datafunction.AbsDataFunction;
import org.twz.datafunction.DataCentre;
import org.twz.exception.IncompleteConditionException;
import org.twz.prob.DistributionManager;
import org.twz.prob.IDistribution;
import java.util.*;


/**
 *
 *
 * Created by TimeWz on 2017/4/17.
 */
public class DistributionLoci extends Loci implements Bindable {
    private final List<String> Parents;
    private final String Distribution;
    private final String DistributionFunction;
    private AbsDataFunction DataFunction;

    public DistributionLoci(String name, String dist) {
        super(name);
        Distribution = dist;
        Parents = parseParents(dist);

        DistributionFunction = Distribution.replaceAll("\\s+", "")
                .replaceAll("([()])", " ")
                .split(" ")[0];

    }

    public DistributionLoci(String name, String dist, Collection<String> parents) {
        super(name);
        Distribution = dist;
        Parents = new ArrayList<>(parents);
        DistributionFunction = Distribution.replaceAll("\\s+", "")
                .replaceAll("([()])", " ")
                .split(" ")[0];
    }

    @Override
    public List<String> getParents() {
        return Parents;
    }

    @Override
    public double evaluate(Map<String, Double> pas) throws IncompleteConditionException {
        return findDistribution(pas).logProb(pas.get(getName()));
    }

    @Override
    public double evaluate(Chromosome chromosome) throws IncompleteConditionException {
        return findDistribution(chromosome).logProb(chromosome.getDouble(getName()));
    }

    @Override
    public double render(Map<String, Double> pas) throws IncompleteConditionException {
        return findDistribution(pas).sample();
    }

    @Override
    public double render(Chromosome chromosome) throws IncompleteConditionException {
        return findDistribution(chromosome).sample();
    }

    @Override
    public double render() {
        return findDistribution(getDefinition()).sample();
    }

    @Override
    public void fill(Chromosome chromosome) throws IncompleteConditionException {
        IDistribution dist = findDistribution(chromosome);
        double v = dist.sample();
        chromosome.put(getName(), v);
        chromosome.addLogPriorProb(dist.logProb(v));
    }

    public IDistribution findDistribution(Map<String, Double> pas) throws IncompleteConditionException {
        String f = Distribution;
        for (String par : Parents) {
            try {
                f = f.replaceAll("\\b" + par + "\\b", pas.get(par).toString());
            } catch (NullPointerException e) {
                throw new IncompleteConditionException(par);
            }
        }
        return findDistribution(f);
    }

    public IDistribution findDistribution(Chromosome pas) throws IncompleteConditionException {
        String f = Distribution;
        for (String par : Parents) {
            try {
                f = f.replaceAll("\\b" + par + "\\b", "" + pas.getDouble(par));
            } catch (NullPointerException e) {
                throw new IncompleteConditionException(par);
            }
        }
        return findDistribution(f);
    }


    private IDistribution findDistribution(String f) {
        f = f.replaceAll("\\s+", "");
        String code = f.replaceAll("\\s+", "");
        code = code.replaceAll("([()])", " ");
        String[] mat = code.split(" ");

        String[] args = mat[1].split(",");
        if (DataFunction == null) {
            return DistributionManager.parseDistribution(f, DistributionFunction, args);
        } else {
            double[] ds = new double[args.length];
            for (int i = 0; i < args.length; i++) {
                ds[i] = Double.parseDouble(args[i]);
            }
            return DataFunction.getSampler(ds);
        }
    }



    @Override
    public String getDefinition() {
        return getName() + "~" + Distribution;
    }

    @Override
    public String toString() {
        return getName() + ": " + Distribution;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject js = super.toJSON();
        js.put("Type", "Sampler");
        js.put("Parents", new JSONArray(getParents()));
        return js;
    }

    @Override
    public void bindDataCentre(DataCentre dataCentre) {
        try {
            DataFunction = dataCentre.getDataFunction(DistributionFunction);
        } catch (NullPointerException ignored) {

        }
    }

    @Override
    public void bindDataFunction(String name, AbsDataFunction df) {
        if (name.equals(DistributionFunction)) {
            DataFunction = df;
        }
    }
}
