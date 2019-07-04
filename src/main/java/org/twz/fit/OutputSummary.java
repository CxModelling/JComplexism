package org.twz.fit;


import org.apache.commons.math3.stat.StatUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.BayesianModel;
import org.twz.dag.Chromosome;

import org.twz.io.IO;

import org.twz.util.Statistics;

import java.util.*;
import java.util.stream.Collectors;

import static org.twz.util.Statistics.quantile;


public class OutputSummary {
	private boolean SequentialData;

	private Map<String, Map<String, Double>> Summary;
	private double Dbar, Dhat, pD, DIC_ext, DIC_app;
	private int SampleSize, NodeSize;
	
	public OutputSummary(BayesianModel bm, boolean seq) {
		SequentialData = seq;
		List<Chromosome> posterior = bm.getResults();
		List<String> nodes = bm.getMovableNodes().stream().map(e->e.Name).collect(Collectors.toList());
		Chromosome mean = meanNodes(posterior, nodes, bm);

		SampleSize = posterior.size();
		NodeSize = nodes.size();
		summariseNodes(posterior, nodes);
		calculateDIC(posterior, mean);
	}

	private void summariseNodes(List<Chromosome> posterior, List<String> nodes) {
		Summary = new LinkedHashMap<>();

		for (String node : nodes) {
			double[] vs = getValues(posterior, node);
			Summary.put(node, summariseNode(vs));
		}
	}

	private Map<String, Double> summariseNode(double[] vs) {
		int ess = (SequentialData)? essSeq(vs): 0;

		double[] sorted = vs.clone();
		Arrays.sort(sorted);
		Map<String, Double> summary = new LinkedHashMap<>();
		summary.put("Mean", StatUtils.mean(vs));
		summary.put("Std", Math.sqrt(StatUtils.variance(vs)));
		summary.put("q025", quantile(sorted,0.025));
		summary.put("q250", quantile(sorted,0.25));
		summary.put("q500", quantile(sorted,0.50));
		summary.put("q750", quantile(sorted,0.75));
		summary.put("q975", quantile(sorted,0.975));
		summary.put("ESS", (double) ess);

		return summary;
	}

	void setESS(int ess) {
		Summary.values().forEach(v->v.replace("ESS", (double) ess));
	}

	private Chromosome meanNodes(List<Chromosome> posterior, List<String> nodes, BayesianModel bm) {
		Chromosome chr = bm.samplePrior();
		Map<String, Double> means = new HashMap<>();
		for (String node : nodes) {
			means.put(node, StatUtils.mean(getValues(posterior, node)));
		}
		chr.impulse(means);
		bm.evaluateLogPrior(chr);
		bm.evaluateLogLikelihood(chr);
		return chr;
	}

	private double[] getValues(List<Chromosome> posterior, String key) {
		return posterior.stream().mapToDouble(e->e.getDouble(key)).toArray();
	}

	private void calculateDIC(List<Chromosome> posterior, Chromosome mean) {
		double[] div = new double[NodeSize];
		for (int i = 0; i < NodeSize; i++) {
			div[i] = -2*posterior.get(i).getLogLikelihood();
		}
		Dbar = StatUtils.mean(div);
		Dhat = -2*mean.getLogLikelihood();
		pD = StatUtils.variance(div)/2;
		DIC_ext = 2*Dbar - Dhat;
		DIC_app = Dbar + pD;
	}

	public void println(){
		IO.DoublePrecision = 5;

		System.out.printf("%15s %10s %10s %10s %10s %10s %10s %10s %10s",
				"Name", "mu", "sd", "q025", "q250", "q500", "q750", "q975", "ESS");
		System.out.println();
		System.out.println("----------------------------------------------" +
				"------------------------------------------------------------");

		for (Map.Entry<String, Map<String, Double>> e: Summary.entrySet()){
			System.out.printf("%15s ", e.getKey());
			System.out.print(e.getValue().values().stream().map(d->String.format("%10g", d))
					.collect(Collectors.joining(" ")));
			System.out.println();
		}

		System.out.println("Nodes: "+ NodeSize);
		System.out.println("Samples: "+ SampleSize);
		System.out.println("D_hat: "+ IO.doubleFormat(Dhat));
		System.out.println("D_bar: "+ IO.doubleFormat(Dbar));
		System.out.println("pD: "+ IO.doubleFormat(pD));
		System.out.println("DIC: "+ IO.doubleFormat(DIC_ext));
		System.out.println("Approx. DIC: "+ IO.doubleFormat(DIC_app));
	}

	public void outputSummaryCSV(String file_path) {
		StringBuilder str = new StringBuilder();
		str.append("Node,Mean,SD,Q2.5,Q25,Q50,Q75,Q97.5,ESS\n");

		for(Map.Entry<String, Map<String, Double>> e:  this.Summary.entrySet()){
			str.append(e.getKey());
			for (double i: e.getValue().values()){
				str.append(", ").append(i);
			}
			str.append("\n");
		}
		//str.append("DIC: " + this.Dic);
		IO.writeText(str.toString(), file_path);
	}

	public JSONObject outputGoodnessOfFit() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("Nodes", NodeSize);
		obj.put("Samples", SampleSize);
		obj.put("DIC", DIC_ext);
		obj.put("DIC_app", DIC_app);
		return obj;
	}

	private int essSeq(double[] xs) {
		int n = xs.length;
		xs = Statistics.add(xs, -Statistics.mean(xs));
		int max_diff = Math.min(n-1, 1000);

		double v = StatUtils.variance(xs);
		double rs = 0;

		for (int i = 1; i < max_diff; i++) {
			rs += calculateRho(xs, n, i, v);
		}

		rs = Math.max(rs, 0);
		return (int) Math.min(Math.round(n/(1+2*rs)), n);
	}

	private double calculateRho(double[] xs, int n, int k, double v) {
		double vk = 0;
		for (int i = 0; i < n-k; i++) {
			vk += xs[i] * xs[i+k];
		}
		return vk/(n-k)/v;
	}

	private int essPara(double[] xs) {

		return SampleSize;
	}
}
