package org.twz.dag;

import org.twz.prob.IDistribution;
import org.twz.dag.loci.DistributionLoci;
import org.twz.dag.loci.Loci;
import org.twz.statistic.Statistics;

import java.util.*;

/**
 *
 * Created by TimeWz on 2017/4/22.
 */
public class BayesianModel implements IParameterModel{

    private static int DefaultSizeMC = 100;

    private final DirectedAcyclicGraph DAG;

    private List<String> Mediator;
    private List<String> Evidence;
    private boolean NeedsMC;
    private Map<String, Double> InputData;

    public BayesianModel(DirectedAcyclicGraph dag, Collection<String> evi) {
        DAG = dag;
        Evidence = new ArrayList<>(evi);
        Mediator = findMediators();
        NeedsMC = Mediator.stream().anyMatch(DAG::isDistribution);
        InputData = new HashMap<>();
    }

    public boolean setInputData(Map<String, Double> dat) {
        if (dat.keySet().containsAll(Evidence)) {
            InputData.putAll(dat);
            return true;
        }
        return false;
    }

    public Gene samplePrior() {
        Map<String, Double> vs = new HashMap<>();
        IDistribution dist;
        Loci lo;
        double prior=0, value;
        for (String loci: DAG.getOrder()) {
            if (Mediator.contains(loci) | Evidence.contains(loci)) {
                continue;
            }
            lo = DAG.getLoci(loci);
            if (lo instanceof DistributionLoci) {
                dist = ((DistributionLoci) lo).findDistribution(vs);
                value = dist.sample();
                vs.put(loci, value);
                prior += dist.logpdf(value);
            } else {
                vs.put(loci, lo.sample(vs));
            }
        }
        return new Gene(vs, prior);
    }

    public Map<String, IDistribution> sampleDistribution() {
        Map<String, Double> vs = new HashMap<>();
        Map<String, IDistribution> ds = new HashMap<>();
        IDistribution dist;
        Loci lo;

        for (String loci: DAG.getOrder()) {
            if (Mediator.contains(loci) | Evidence.contains(loci)) {
                continue;
            }
            lo = DAG.getLoci(loci);
            if (lo instanceof DistributionLoci) {
                dist = ((DistributionLoci) lo).findDistribution(vs);
                ds.put(loci, dist);
                vs.put(loci, dist.sample());
            } else {
                vs.put(loci, lo.sample(vs));
            }
        }
        return ds;
    }

    public double evaluatePrior(Gene gene) {
        return DAG.evaluate(gene.getLocus());
    }

    public double evaluateLikelihood(Gene gene) {
        if (InputData.isEmpty()) return 0;

        double li;
        Map<String, Double> fixed = new HashMap<>(gene.getLocus());
        fixed.putAll(InputData);

        if (NeedsMC) {
            double[] lis = new double[DefaultSizeMC];
            Map<String, Double> mc;
            for (int i = 0; i < DefaultSizeMC; i++) {
                li = 0;
                mc = DAG.sample(fixed);
                for (String loci: DAG.getOrder()) {
                    if (Evidence.contains(loci) | Mediator.contains(loci))
                        li += DAG.getLoci(loci).evaluate(mc);
                }
                lis[i] = li;
            }
            return Statistics.lse(lis) - Math.log(DefaultSizeMC);

        } else {
            li = 0;
            fixed = DAG.sample(fixed);
            for (String loci: Evidence) {
                li += DAG.getLoci(loci).evaluate(fixed);
            }
            return li;
        }
    }

    @Override
    public DirectedAcyclicGraph getDAG() {
        return DAG;
    }


    public void print() {
        System.out.println("DAG:");
        DAG.print();
        System.out.println("Mediator:"+ Mediator);
        System.out.println("Evidence:"+ Evidence);
    }

    private List<String> findMediators() {
        Set<String> des = new HashSet<>();
        String last = "";
        for (String loci: DAG.getOrder()) {
            if (Evidence.contains(loci) | des.contains(loci)) {
                des.addAll(DAG.getChildren(loci));
                last = loci;
            }
        }

        List<String> med = new ArrayList<>();
        for (String loci: des) {
            if (loci.equals(last)) break;
            if (!Evidence.contains(loci)) {
                med.add(loci);
            }
        }
        return med;
    }
}
