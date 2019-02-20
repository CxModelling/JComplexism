package org.twz.prob;

import org.twz.util.Statistics;

public class Const implements IDistribution {
	private String Name;
	private final double K;

	public Const(String name, Double k){
		Name = name;
		K = k;
	}

	public Const(double k) {
		this(null, k);
		Name = String.format("k(%1$s)", K);
	}

	@Override
	public String getName() {
		return Name;
	}

	@Override
	public double sample() {
		return K;
	}

	@Override
	public double[] sample(int n) {
		return Statistics.rep(K, n);
	}

	@Override
	public double logpdf(double rv) {
		if (rv == K) {
			return 0;
		} else {
			return Double.NEGATIVE_INFINITY;
		}
	}

	@Override
	public String getDataType() {
		return "Double";
	}

	@Override
	public double getUpper() {
		return K;
	}

	@Override
	public double getLower() {
		return K;
	}

	@Override
	public double getMean() {
		return K;
	}

	@Override
	public double getStd() {
		return 0;
	}
}
