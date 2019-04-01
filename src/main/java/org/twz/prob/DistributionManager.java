package org.twz.prob;

import org.twz.factory.Workshop;
import org.twz.factory.arguments.AbsArgument;
import org.twz.factory.arguments.DoubleArg;
import org.twz.factory.arguments.IntegerArg;

import java.text.DecimalFormat;


/**
 *
 * Created by TimeWz on 2017/4/17.
 */
public class DistributionManager {
    private static DecimalFormat df = new DecimalFormat("0.0");
    private static Workshop Distributions = new Workshop();

    static {
        df.setMaximumFractionDigits(6);

        AbsArgument[] ags;

        ags = new AbsArgument[]{new DoubleArg("rate")};
        Distributions.register("exp", Exponential.class, ags);

        ags = new AbsArgument[]{new DoubleArg("k")};
        Distributions.register("k", Const.class, ags);

        ags = new AbsArgument[]{new DoubleArg("df")};
        Distributions.register("chisq", Chi2.class, ags);

        ags = new AbsArgument[]{new DoubleArg("df")};
        Distributions.register("t", T.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("m"),
                new DoubleArg("a"),
                new DoubleArg("b")};
        Distributions.register("triangle", Triangle.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("alpha"),
                new DoubleArg("beta")};
        Distributions.register("weibull", Weibull.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("mean"),
                new DoubleArg("sd")};
        Distributions.register("norm", Normal.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("meanlog"),
                new DoubleArg("sdlog")};
        Distributions.register("lnorm", Lognormal.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("min"),
                new DoubleArg("max")};
        Distributions.register("unif", Uniform.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("shape"),
                new DoubleArg("rate")};
        Distributions.register("gamma", Gamma.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("shape1"),
                new DoubleArg("shape2")};
        Distributions.register("beta", Beta.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("lambda")};
        Distributions.register("pois", Poisson.class, ags);

        ags = new AbsArgument[]{
                new IntegerArg("size"),
                new DoubleArg("prob")};
        Distributions.register("binom", Binom.class, ags);

        ags = new AbsArgument[]{
                new DoubleArg("lambda")};
        Distributions.register("geom", Geometric.class, ags);
    }


    public static IWalkable parseDistribution(String input) {
        String code = input.replaceAll("\\s+", "");
        code = code.replaceAll("(\\(|\\))", " ");
        String[] mat = code.split(" ");

        String[] args = mat[1].split(",");
        return (IWalkable) Distributions.create(input, mat[0], args);
    }

}
