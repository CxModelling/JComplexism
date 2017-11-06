package pcore;

import org.json.JSONObject;
import pcore.distribution.IDistribution;
import pcore.loci.DistributionLoci;
import pcore.loci.Loci;
import utils.Statistics;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/4/23.
 */
public class SimulationModel implements IParameterModel {
    private final DirectedAcyclicGraph DAG;
    private final static HashMap<String, Double> NullCon = new HashMap<>();

    public SimulationModel(DirectedAcyclicGraph dag) {
        DAG = dag;
    }

    public SimulationModel(String script) throws ScriptException {
        this(new DirectedAcyclicGraph(script));
    }

    public SimulationModel(JSONObject js) throws ScriptException {
        this(new DirectedAcyclicGraph(js));
    }

    public String getName() {
        return DAG.getName();
    }

    public ParameterCore sampleCore() {
        return sampleCore(NullCon);
    }

    public ParameterCore sampleCore(Map<String, Double> cond) {
        Map<String, Double> vs = new HashMap<>(cond);
        Map<String, IDistribution> ds = new HashMap<>();
        Loci l;
        for (String loci: DAG.getOrder()) {
            l = DAG.getLoci(loci);
            if (DAG.getLeaves().contains(loci) & l instanceof DistributionLoci) {
                ds.put(loci, ((DistributionLoci) l).findDistribution(vs));
            } else {
                vs.put(loci, l.sample(vs));
            }
        }

        ParameterCore Core = new ParameterCore(vs, ds);
        Core.setLogPriorProb(DAG.evaluate(Core.getLocus()));
        return Core;
    }



    public List<ParameterCore> mutate(List<ParameterCore> pc) {
        List<String> names = new ArrayList<>(pc.get(0).getLocus().keySet());
        Map<String, double[]> ps = new HashMap<>();
        double[] arr;
        for (String name: names) {
            arr = pc.stream().mapToDouble(e->e.get(name)).toArray();
            if (DAG.isDistribution(name)) {
                ps.put(name, Statistics.jitter(arr));
            }
        }

        List<ParameterCore> pcx = new ArrayList<>();
        Map<String, Double> v;
        for (int i = 0; i < ps.size(); i++) {
            v = new HashMap<>();
            for (Map.Entry<String, double[]> ent: ps.entrySet()) {
                v.put(ent.getKey(), ent.getValue()[i]);
            }
            pcx.add(reformCore(v));
        }
        return pcx;
    }

    public ParameterCore intervene_core(ParameterCore pc, Map<String, Double> intervention) {
        Map<String, Double> vs = new HashMap<>();
        vs.putAll(intervention);
        Map<String, IDistribution> ds = new HashMap<>();
        Set<String> shocked = new HashSet<>();
        intervention.keySet().forEach(e->shocked.addAll(DAG.getDescendants(e)));

        Loci l;
        for (String loci: DAG.getOrder()) {
            l = DAG.getLoci(loci);
            if (DAG.getLeaves().contains(loci) & l instanceof DistributionLoci) {
                ds.put(loci, ((DistributionLoci) l).findDistribution(vs));
            } else if (shocked.contains(loci)){
                vs.putIfAbsent(loci, l.sample(vs));
            } else {
                vs.putIfAbsent(loci, pc.get(loci));
            }
        }

        ParameterCore Core = new ParameterCore(vs, ds);
        Core.setLogPriorProb(DAG.evaluate(Core.getLocus()));
        return Core;
    }


    public ParameterCore reformCore(Map<String, Double> vs) {
        Map<String, IDistribution> ds = new HashMap<>();

        for (String loci: DAG.getLeaves()) {
            ds.put(loci, ((DistributionLoci) DAG.getLoci(loci)).findDistribution(vs));
        }

        ParameterCore Core = new ParameterCore(vs, ds);
        Core.setLogPriorProb(DAG.evaluate(Core.getLocus()));
        return Core;
    }

    public ParameterCore reformCore(ParameterCore core) {
        return reformCore(core.getLocus());
    }

    @Override
    public DirectedAcyclicGraph getDAG() {
        return DAG;
    }

    public JSONObject toJSON() {
        return DAG.toJSON();
    }
}
