package utils;


import org.json.JSONArray;
import org.json.JSONObject;
import pcore.Gene;
import utils.json.AdapterJSONObject;

import java.util.*;
import static utils.Statistics.*;

public class Summarizer implements AdapterJSONObject {

	private Map<String, double[]> Summary;
	private double Dic;
	private double[] LogL;
	private int SizeLocus;
	
	public Summarizer(List<Gene> genes){
		SizeLocus = genes.get(0).getSize();
		LogL = new double[genes.size()];
		this.summarize(genes);
	}
	
	public Map<String, double[]> getSummary() {
		return Summary;
	}

	public Set<String> getNames() {
		return Summary.keySet();
	}

	private void summarize(List<Gene> Chrs){

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

    private double[] getLoci(String s, List<Gene> Chrs){
        return Chrs.stream().mapToDouble(e->e.get(s)).toArray();
    }
	
	
	private double[] summarizeLocus(double[] Locus){
		Arrays.sort(Locus);
		double[] summary = new double[7];
		summary[0] = mean(Locus);
		summary[1] = Math.sqrt(var(Locus));
		summary[2] = quantile(Locus,0.025);
		summary[3] = quantile(Locus,0.25);
		summary[4] = quantile(Locus,0.50);
		summary[5] = quantile(Locus,0.75);
		summary[6] = quantile(Locus,0.975);
		return summary;
	}
	
	
	public double[] getSummary(String i) {
		return Summary.get(i);
	}


	public void println(){
		System.out.println("Name \t   mu\t   sd\t q025\t q250\t q500\t q750\t q975");
		for (Map.Entry<String, double[]> e: Summary.entrySet()){
            System.out.print(e.getKey()+"\t");
            for(int j = 0 ; j < 7 ; j ++){
                System.out.print(round(e.getValue()[j],4)+"\t");
            }
            System.out.println("");
        }

		System.out.println("DIC: "+ this.Dic);
	}
	
	private void DIC(){
		double Dbar = -2*mean(LogL);
		double pD = var(LogL);
		this.Dic = Dbar + pD;
	}
	
	

	
	private double round(double val, int k){
		double v = val * Math.pow(10, k);
		v = Math.round(v);
		v /= Math.pow(10, k);
		return v;
	}

	public void output(String file){
		StringBuilder str = new StringBuilder();
		str.append("Name, Mean,SD,2.5,25,50,75,97.5\n");

		for(Map.Entry<String, double[]> e:  this.Summary.entrySet()){
			str.append(e.getKey());
			for (double i: e.getValue()){
				str.append(", "+round(i, 5));
			}
			str.append("\n");
		}
		//str.append("DIC: " + this.Dic);
		IO.writeText(str.toString(), file);

	}
	
	public void json(String file){
		JSONArray jsa = new JSONArray();
		jsa.put(Summary);

		JSONObject obj = new JSONObject();
		obj.put("Summary", jsa);
		obj.put("DIC", Dic);
		obj.put("Size", SizeLocus);
		IO.writeJSON(obj, file);
	}

	@Override
	public JSONObject toJSON() {
		JSONArray jsa = new JSONArray();
		jsa.put(Summary);

		JSONObject obj = new JSONObject();
		obj.put("Summary", jsa);
		obj.put("DIC", Dic);
		obj.put("Size", SizeLocus);
		return obj;
	}
}
