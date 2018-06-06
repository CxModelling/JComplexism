package org.twz.prob;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

public class Triangle implements IDistribution {

	private static RandomGenerator RNG = new MersenneTwister();
	private RandomGenerator rng;
	private double A, B, M, FM;
	private String Name;

	public Triangle(String name, Double a, Double m, Double b) {
		Name = name;
		A = a;
		B = b;
		M = m;
		FM = (M-A)*(M-A)/(B-A)/(B-M);
		rng = RNG;
	}

	public Triangle(double a, double m, double b) {
		this(null, a, m, b);
		Name = String.format("triangle(%1$s,%2$s,%3$s)", A, M, B);
	}

	@Override
	public String getName() {
		return Name;
	}

	@Override
	public double sample() {
		double u = rng.nextDouble();
		if (u < FM) {
			return A + Math.sqrt(u*(B-A)*(M-A));
		} else {
			return B - Math.sqrt((1-u)*(B-A)*(B-M));
		}
	}

	@Override
	public double[] sample(int n) {
		double[] rvs = new double[n];
		for (int i = 0; i < n; i++) {
			rvs[i] = sample();
		}
		return rvs;
	}

	@Override
	public double logpdf(double rv) {
		if(rv < A || rv > B){
			return Double.NEGATIVE_INFINITY;
		}
		double den;
		if(rv > M){
			den = 2*(rv-A)/(B-A)/(M-A);
		}else if(rv == M){
			den = 2/(B-A);
		}else{
			den = 2*(B-rv)/(B-A)/(B-M);
		}

		return Math.log(den);
	}

	@Override
	public String getDataType() {
		return "Double";
	}

	@Override
	public double getUpper() {
		return B;
	}

	@Override
	public double getLower() {
		return A;
	}

	@Override
	public double getMean() {
		return (A+B+M)/3;
	}

	@Override
	public double getStd() {
		return Math.sqrt((A*A+B*B+M*M-A*B-A*M-B*M)/18);
	}
}
