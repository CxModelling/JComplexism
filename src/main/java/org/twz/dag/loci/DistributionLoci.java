package org.twz.dag.loci;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mariuszgromada.math.mxparser.Expression;
import org.twz.dag.Chromosome;
import org.twz.exception.IncompleteConditionException;
import org.twz.prob.DistributionManager;
import org.twz.prob.IDistribution;
import org.twz.prob.IWalkable;
import java.util.*;


/**
 *
 *
 * Created by TimeWz on 2017/4/17.
 */
public class DistributionLoci extends Loci {
    private final List<String> Parents;
    private final String Distribution;
    private final Expression DE;

    public DistributionLoci(String name, String dist) {
        super(name);
        Distribution = dist;
        DE = new Expression(Distribution);
        Parents = parseParents(dist);
    }

    public DistributionLoci(String name, String dist, Collection<String> parents) {
        super(name);
        Distribution = dist;
        DE = new Expression(Distribution);
        Parents = new ArrayList<>(parents);
    }

    @Override
    public List<String> getParents() {
        return Parents;
    }

    @Override
    public double evaluate(Map<String, Double> pas) {
        return findDistribution(pas).logProb(pas.get(getName()));
    }

    @Override
    public double evaluate(Chromosome chromosome) {
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
    public double render() throws IncompleteConditionException {
        return Double.parseDouble(null); // todo
    }

    @Override
    public void fill(Chromosome chromosome) {
        IDistribution dist = findDistribution(chromosome);
        double v = 0;
        try {
            v = dist.sample();
        } catch (IncompleteConditionException e) {
            e.printStackTrace();
        }
        chromosome.put(getName(), v);
        chromosome.addLogPriorProb(dist.logProb(v));
    }

    public IWalkable findDistribution(Map<String, Double> pas) {
        String f = Distribution;
        for (String par : Parents) {
            f = f.replaceAll("\\b" + par + "\\b", pas.get(par).toString());
        }

        f = f.replaceAll("\\s+", "");

        return DistributionManager.parseDistribution(f);
    }

    public IDistribution findDistribution(Chromosome pas) {
        String f = Distribution;
        for (String par : Parents) {
            f = f.replaceAll("\\b" + par + "\\b","" + pas.getDouble(par));
        }

        f = f.replaceAll("\\s+", "");

        return DistributionManager.parseDistribution(f);
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
}
