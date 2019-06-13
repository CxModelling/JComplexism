package org.twz.fit;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.twz.dag.Chromosome;
import org.twz.fit.mcmc.EffectiveSampleSize;
import org.twz.io.IO;
import org.twz.io.AdapterJSONObject;

import java.util.*;
import java.util.stream.Collectors;

import static org.twz.util.Statistics.*;

public class Summarizer implements AdapterJSONObject {

	private Map<String, Map<String, Double>> Summary;
	private double DIC;
	private double[] LogL;
	private int SizeLocus;
	
	public Summarizer(List<Chromosome> chromosomes){
		SizeLocus = chromosomes.get(0).getSize();
		LogL = new double[chromosomes.size()];
		this.summarize(chromosomes);
	}

	private void summarize(List<Chromosome> Chrs){

		Summary = new TreeMap<>();
        Collection<String> Names = Chrs.get(0).getLocus().keySet();
		for(String name: Names){
			this.Summary.put(name, summarizeLocus(getLoci(name, Chrs)));
		}
        for (int i = 0; i < Chrs.size(); i++) {
            LogL[i] = Chrs.get(i).getLogLikelihood();
        }
        this.DIC();
	}

    private double[] getLoci(String s, List<Chromosome> chromosomes){
        return chromosomes.stream().mapToDouble(e->e.getDouble(s)).toArray();
    }
	
	
	protected Map<String, Double> summarizeLocus(double[] locus){
		double[] sorted = locus.clone();
		Arrays.sort(sorted);

		Map<String, Double> summary = new LinkedHashMap<>();
		summary.put("Mean", mean(locus));
		summary.put("Std", Math.sqrt(var(locus)));
		summary.put("q025", quantile(sorted,0.025));
		summary.put("q250", quantile(sorted,0.25));
		summary.put("q500", quantile(sorted,0.50));
		summary.put("q750", quantile(sorted,0.75));
		summary.put("q975", quantile(sorted,0.975));
		summary.put("ESS", (double) EffectiveSampleSize.calculate(locus));

		return summary;
	}

	public void println(){
		IO.DoublePrecision = 3;
		System.out.println("Name \t   mu\t   sd\t q025\t q250\t q500\t q750\t q975\t ESS");
		for (Map.Entry<String, Map<String, Double>> e: Summary.entrySet()){
            System.out.print(e.getKey()+"\t\t");
			System.out.print(e.getValue().values().stream().map(d->IO.doubleFormat(d, 4))
					.collect(Collectors.joining("\t")));
            System.out.println();
        }

		System.out.println("DIC: "+ IO.doubleFormat(this.DIC,4));
	}
	
	private void DIC(){
		double Dbar = -2*mean(LogL);
		double pD = var(LogL);
		this.DIC = Dbar + pD;
	}
	
	

	
	private double round(double val, int k){
		double v = val * Math.pow(10, k);
		v = Math.round(v);
		v /= Math.pow(10, k);
		return v;
	}

	public void outputCSV(String file){
		StringBuilder str = new StringBuilder();
		str.append("Name,Mean,SD,2.5,25,50,75,97.5,ESS\n");

		for(Map.Entry<String, Map<String, Double>> e:  this.Summary.entrySet()){
			str.append(e.getKey());
			for (double i: e.getValue().values()){
				str.append(", ").append(round(i, 5));
			}
			str.append("\n");
		}
		//str.append("DIC: " + this.Dic);
		IO.writeText(str.toString(), file);

	}
	
	public void json(String file) throws JSONException {
		JSONArray jsa = new JSONArray();
		jsa.put(Summary);

		JSONObject obj = new JSONObject();
		obj.put("Summary", jsa);
		obj.put("DIC", DIC);
		obj.put("Size", SizeLocus);
		IO.writeJSON(obj, file);
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONArray jsa = new JSONArray();
		jsa.put(Summary);

		JSONObject obj = new JSONObject();
		obj.put("Summary", jsa);
		obj.put("DIC", DIC);
		obj.put("Size", SizeLocus);
		return obj;
	}
}
